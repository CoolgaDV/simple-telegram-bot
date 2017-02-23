package cdv.stb;

import cdv.stb.protocol.Chat;
import cdv.stb.protocol.Message;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Coolga
 *         19.02.2017 11:07
 */
public class SubscriptionTriggerTest {

    private final TelegramApiClient clientMock = mock(TelegramApiClient.class);

    private final SubscriptionManager subscriptionManagerMock = mock(SubscriptionManager.class);

    private final SubscriptionTrigger trigger = new SubscriptionTrigger(
            clientMock,
            subscriptionManagerMock);

    @Test
    public void testMatch() {
        assertTrue(trigger.match(new Message(0, null, null, 0, "Бот, подписка")));
    }

    @Test
    public void testNotMatch() {
        assertFalse(trigger.match(new Message(0, null, null, 0, null)));
        assertFalse(trigger.match(new Message(0, null, null, 0, "")));
        assertFalse(trigger.match(new Message(0, null, null, 0, "подписка")));
        assertFalse(trigger.match(new Message(0, null, null, 0, "Бот, Подписка")));
    }

    @Test
    public void testFireSubscriptionRegistrationSucceeded() {
        testFire(true, "Подписка успешно зарегистрирована");
    }

    @Test
    public void testFireSubscriptionRegistrationFailed() {
        testFire(false, "Не удалось зарегистрировать подписку");
    }

    private void testFire(boolean subscribedSuccessfully, String expectedMessage) {

        when(subscriptionManagerMock.registerSubscription(anyLong()))
                .thenReturn(subscribedSuccessfully);

        Chat chat = new Chat(42, null, null, null, null);
        Message message = new Message(0, null, chat, 0, null);
        trigger.fire(message);

        verify(clientMock).sendMessage(expectedMessage, 42);
        verify(subscriptionManagerMock).registerSubscription(42);
    }

}