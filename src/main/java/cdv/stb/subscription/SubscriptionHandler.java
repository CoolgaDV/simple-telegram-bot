package cdv.stb.subscription;

import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.common.MessageHandler;
import cdv.stb.telegram.protocol.Message;

/**
 * Handler for receiving subscription requests
 *
 * @author Dmitry Coolga
 *         19.02.2017 10:39
 */
public class SubscriptionHandler implements MessageHandler {

    private final TelegramApiClient apiClient;
    private final SubscriptionManager subscriptionManager;

    public SubscriptionHandler(TelegramApiClient apiClient,
                               SubscriptionManager subscriptionManager) {
        this.apiClient = apiClient;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public boolean match(Message message) {
        return "Бот, подписка".equals(message.getText());
    }

    @Override
    public void handle(Message message) {
        long chatId = message.getChat().getId();
        boolean succeeded = subscriptionManager.registerSubscription(chatId);
        String text = succeeded ?
                "Подписка успешно зарегистрирована" :
                "Не удалось зарегистрировать подписку";
        apiClient.sendMessage(text, chatId);
    }

}
