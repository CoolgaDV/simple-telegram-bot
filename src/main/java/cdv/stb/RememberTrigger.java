package cdv.stb;

import cdv.stb.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple trigger sends remember acknowledgement message.
 *
 * @author Dmitry Coolga
 *         05.02.2017 11:07
 */
public class RememberTrigger implements Trigger {

    private static final Logger log = LoggerFactory.getLogger(RememberTrigger.class);

    private final TelegramApiClient apiClient;

    public RememberTrigger(TelegramApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public boolean match(Message message) {
        String text = message.getText();
        return text != null && text.startsWith("Помнишь");
    }

    @Override
    public void fire(Message message) {
        String output = apiClient.sendMessage(
                "Помню !",
                message.getChat().getId());
        log.info("Response sent. Result: {}", output);
    }

}
