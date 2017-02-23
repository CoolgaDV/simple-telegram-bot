package cdv.stb.listener;

import cdv.stb.common.MessageHandler;
import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.exception.MessageFormatException;
import cdv.stb.exception.NetworkException;
import cdv.stb.exception.RequestFailureException;
import cdv.stb.telegram.protocol.Message;
import cdv.stb.telegram.protocol.Response;
import cdv.stb.telegram.protocol.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

    private final AtomicLong updateCounter = new AtomicLong(0);
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private final MessageHandler[] handlers;
    private final TelegramApiClient apiClient;

    private final int networkFailurePauseMinutes;
    private final int requestFailureThreshold;
    private final int pollingTimeoutSeconds;

    private volatile boolean active = true;

    public MessageListenerTask(TelegramApiClient apiClient,
                               int networkFailurePauseMinutes,
                               int requestFailureThreshold,
                               int pollingTimeoutSeconds,
                               MessageHandler... handlers) {
        this.apiClient = apiClient;
        this.networkFailurePauseMinutes = networkFailurePauseMinutes;
        this.requestFailureThreshold = requestFailureThreshold;
        this.pollingTimeoutSeconds = pollingTimeoutSeconds;
        this.handlers = handlers;

    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Shutting down listener task...");
        active = false;
        shutdownLatch.await();
    }

    @Async
    public void start() {
        log.info("Listener task started");
        if ( ! skipStaleUpdates()) {
            return;
        }
        int failureCounter = 0;
        while (active) {
            try {
                listen();
                failureCounter = 0;
            } catch (NetworkException ex) {
                log.warn("Network failure. Next attempt will be made in {} minutes", 
                        networkFailurePauseMinutes, ex);
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
        log.info("Listener task is shutdown");
        shutdownLatch.countDown();
    }

    private boolean skipStaleUpdates() {
        try {
            List<Result> updates = apiClient.getUpdates(0, 0).getResults();
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
        Response response = apiClient.getUpdates(
                pollingTimeoutSeconds,
                updateCounter.get() + 1);
        for (Result result : response.getResults()) {
            long updateId = result.getUpdateId();
            if (updateCounter.get() < updateId) {
                updateCounter.set(updateId);
            }
            Message message = result.getMessage();
            if (message == null) {
                log.warn("One of received updates has no message: {}", response);
                return;
            }
            String text = message.getText();
            log.info("Got message : {}, author: {}, chatId: {}",
                    text,
                    message.getFrom().getUserName(),
                    message.getChat().getId());
            for (MessageHandler handler : handlers) {
                if (handler.match(message)) {
                    handler.handle(message);
                }
            }
        }
    }
    
    private void fatal(String message, Object... args) {
        Object[] logArgs = new Object[args.length + 1];
        logArgs[0] = message;
        System.arraycopy(args, 0, logArgs, 1, args.length);
        log.error("{}. The application will be terminated", logArgs);
    }

}
