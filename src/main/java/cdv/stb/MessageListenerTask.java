package cdv.stb;

import cdv.stb.exception.MessageFormatException;
import cdv.stb.exception.NetworkException;
import cdv.stb.exception.RequestFailureException;
import cdv.stb.protocol.Message;
import cdv.stb.protocol.Response;
import cdv.stb.protocol.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Telegram message listening routine
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:11
 */
@Component
public class MessageListenerTask {

    private static final Logger log = LoggerFactory.getLogger(MessageListenerTask.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicLong updateCounter = new AtomicLong(0);

    private final TelegramApiClient apiClient;

    private final int networkFailurePauseMinutes;
    private final int requestFailureThreshold;
    private final int pollingTimeoutSeconds;

    public MessageListenerTask(TelegramApiClient apiClient,
                               int networkFailurePauseMinutes,
                               int requestFailureThreshold,
                               int pollingTimeoutSeconds) {
        this.apiClient = apiClient;
        this.networkFailurePauseMinutes = networkFailurePauseMinutes;
        this.requestFailureThreshold = requestFailureThreshold;
        this.pollingTimeoutSeconds = pollingTimeoutSeconds;
    }

    @Async
    public void start() {
        log.info("Listener thread started");
        if ( ! skipStaleUpdates()) {
            return;
        }
        int failureCounter = 0;
        while (true) {
            try {
                listen();
                failureCounter = 0;
            } catch (NetworkException ex) {
                log.warn("Network failure. Next attempt will be made in {} minutes", 
                        networkFailurePauseMinutes);
                try {
                    TimeUnit.MINUTES.sleep(networkFailurePauseMinutes);
                } catch (InterruptedException innerEx) {
                    log.warn("Pause interruption. Execution will be proceeded", ex);
                }
            } catch (MessageFormatException ex) {
                fatal("Message parsing failure", ex);
                return;
            } catch (RequestFailureException ex) {
                failureCounter++;
                log.error("Request failure. Failures counter: {}", failureCounter, ex);
                if (failureCounter > requestFailureThreshold) {
                    fatal("Failures count is greater than {}", requestFailureThreshold);
                    return;
                }
            } catch (Exception ex) {
                fatal("Unexpected error", ex);
                return;
            }
        }
    }

    private boolean skipStaleUpdates() {
        try {
            String response = apiClient.getUpdates(0, 0);
            List<Result> updates = parseResponse(response).getResults();
            log.info("Read stale updates: {}", updates.size());
            updateCounter.set(updates.stream()
                    .mapToLong(Result::getUpdateId)
                    .max().orElse(0));
            return true;
        } catch (Exception ex) {
            fatal("Error while reading stale updates", ex);
            return false;
        }
    }

    private void listen() {
        String updates = apiClient.getUpdates(
                pollingTimeoutSeconds,
                updateCounter.get() + 1);
        Response response = parseResponse(updates);
        for (Result result : response.getResults()) {
            long updateId = result.getUpdateId();
            if (updateCounter.get() < updateId) {
                updateCounter.set(updateId);
            }
            Message message = result.getMessage();
            if (message == null) {
                log.warn("One of received updates has no message: " + updates);
                return;
            }
            String text = message.getText();
            log.info("Got message : {}, author: {}, chatId: {}",
                    text,
                    message.getFrom().getUserName(),
                    message.getChat().getId());
            if (text == null || ! text.startsWith("Помнишь")) {
                continue;
            }
            String output = apiClient.sendMessage(
                    "Помню !",
                    message.getChat().getId());
            log.info("Response sent. Result: {}", output);
        }
    }

    private Response parseResponse(String source) {
        Response response;
        try {
            response = mapper.readValue(source, Response.class);
        } catch (IOException ex) {
            throw new MessageFormatException(source, ex);
        }
        if ( ! response.isSucceeded()) {
            throw new RequestFailureException(source);
        }
        return response;
    }

    
    private void fatal(String message, Object... args) {
        Object[] logArgs = new Object[args.length + 1];
        logArgs[0] = message;
        System.arraycopy(args, 0, logArgs, 1, args.length);
        log.error("{}. The application will be terminated", logArgs);
    }

}
