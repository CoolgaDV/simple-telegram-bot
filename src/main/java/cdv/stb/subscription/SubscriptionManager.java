package cdv.stb.subscription;

import cdv.stb.common.SubscriptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.Set;

/**
 * Component for subscriptions' registration and sending messages for them by schedule
 *
 * @author Dmitry Coolga
 *         12.02.2017 21:37
 */
public class SubscriptionManager {

    private static final String CHAT_ID_STORAGE = "subscription:chat_ids";

    private static final Logger log = LoggerFactory.getLogger(SubscriptionManager.class);

    private final RedisTemplate<String, Long> redisTemplate;
    private final SubscriptionHandler[] handlers;

    public SubscriptionManager(RedisTemplate<String, Long> redisTemplate,
                               SubscriptionHandler... handlers) {
        this.redisTemplate = redisTemplate;
        this.handlers = handlers;
    }

    public boolean registerSubscription(long chatId) {
        try {
            redisTemplate.opsForSet().add(CHAT_ID_STORAGE, chatId);
            log.info("Subscription is registered for chat with ID: {}", chatId);
            return true;
        } catch (Exception ex) {
            log.error("Subscription registration failure for chat with ID: " + chatId, ex);
            return false;
        }
    }

    @Scheduled(cron = "${app.subscription.cron}", zone = "${app.subscription.zone}")
    public void deliverToSubscribers() {
        Set<Long> chatIds = redisTemplate.opsForSet().members(CHAT_ID_STORAGE);
        log.info("Sending messages by subscription for chats with IDs: {}", chatIds);
        for (Long id : chatIds) {
            Arrays.stream(handlers).forEach(handler -> handler.handleSubscription(id));
        }
    }

}
