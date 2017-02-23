package cdv.stb.common;

/**
 * Interface for triggers with subscription support
 *
 * @author Dmitry Coolga
 *         22.02.2017 21:33
 */
public interface TriggerWithSubscription {

    void handleSubscriptionDelivery(long chatId);

}
