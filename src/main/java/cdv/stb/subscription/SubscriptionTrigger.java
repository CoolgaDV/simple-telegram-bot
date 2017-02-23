package cdv.stb.subscription;

import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.common.Trigger;
import cdv.stb.telegram.protocol.Message;

/**
 * Trigger for subscription requests
 *
 * @author Dmitry Coolga
 *         19.02.2017 10:39
 */
public class SubscriptionTrigger implements Trigger {

    private final TelegramApiClient apiClient;
    private final SubscriptionManager subscriptionManager;

    public SubscriptionTrigger(TelegramApiClient apiClient,
                               SubscriptionManager subscriptionManager) {
        this.apiClient = apiClient;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public boolean match(Message message) {
        return "Бот, подписка".equals(message.getText());
    }

    @Override
    public void fire(Message message) {
        long chatId = message.getChat().getId();
        boolean succeeded = subscriptionManager.registerSubscription(chatId);
        String text = succeeded ?
                "Подписка успешно зарегистрирована" :
                "Не удалось зарегистрировать подписку";
        apiClient.sendMessage(text, chatId);
    }

}
