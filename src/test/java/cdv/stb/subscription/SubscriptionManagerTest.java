package cdv.stb.subscription;

import cdv.stb.common.SubscriptionHandler;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Dmitry Coolga
 *         19.02.2017 11:21
 */
public class SubscriptionManagerTest {

    @SuppressWarnings("unchecked")
    private final RedisTemplate<String, Long> redisTemplateMock = mock(RedisTemplate.class);

    @SuppressWarnings("unchecked")
    private final SetOperations<String, Long> setOperationsMock = mock(SetOperations.class);

    @Test
    public void registerSubscriptionSuccessfully() {

        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);

        SubscriptionManager subscriptionManager = new SubscriptionManager(redisTemplateMock);

        assertTrue(subscriptionManager.registerSubscription(42));
        verify(setOperationsMock).add("subscription:chat_ids", 42L);
    }

    @Test
    public void registerSubscriptionWithFailure() {

        when(redisTemplateMock.opsForSet()).thenThrow(
                new RuntimeException("Redis persistence failure"));

        SubscriptionManager subscriptionManager = new SubscriptionManager(redisTemplateMock);

        assertFalse(subscriptionManager.registerSubscription(42));
    }

    @Test
    public void deliverToSubscribers() {

        Set<Long> chatIds = new HashSet<>();
        chatIds.add(42L);

        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.members("subscription:chat_ids")).thenReturn(chatIds);

        SubscriptionHandler handler = mock(SubscriptionHandler.class);

        SubscriptionManager subscriptionManager = new SubscriptionManager(
                redisTemplateMock,
                handler);

        subscriptionManager.deliverToSubscribers();
        verify(handler).handleSubscription(42);
    }

}