package cdv.stb.plain;

import cdv.stb.common.MessageHandler;
import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.telegram.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple handler for sending remember acknowledgement messages.
 *
 * @author Dmitry Coolga
 *         05.02.2017 11:07
 */
public class RememberHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RememberHandler.class);

    private final TelegramApiClient apiClient;

    public RememberHandler(TelegramApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public boolean match(Message message) {
        String text = message.getText();
        return text != null && text.startsWith("Помнишь");
    }

    @Override
    public void handle(Message message) {
        String output = apiClient.sendMessage(
                "Помню !",
                message.getChat().getId());
        log.info("Response sent. Result: {}", output);
    }

}
