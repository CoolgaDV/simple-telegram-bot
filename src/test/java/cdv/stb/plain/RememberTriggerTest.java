package cdv.stb.plain;

import cdv.stb.plain.RememberTrigger;
import cdv.stb.telegram.protocol.Chat;
import cdv.stb.telegram.protocol.Message;
import cdv.stb.telegram.TelegramApiClient;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Coolga
 *         05.02.2017 11:35
 */
public class RememberTriggerTest {

    private final TelegramApiClient clientMock = mock(TelegramApiClient.class);
    private final RememberTrigger trigger = new RememberTrigger(clientMock);

    @Test
    public void testMatch() {
        assertTrue(trigger.match(new Message(0, null, null, 0, "Помнишь")));
        assertTrue(trigger.match(new Message(0, null, null, 0, "Помнишь?")));
    }

    @Test
    public void testNotMatch() {
        assertFalse(trigger.match(new Message(0, null, null, 0, null)));
        assertFalse(trigger.match(new Message(0, null, null, 0, "")));
        assertFalse(trigger.match(new Message(0, null, null, 0, " Помнишь")));
        assertFalse(trigger.match(new Message(0, null, null, 0, "ПОМНИШЬ")));
    }

    @Test
    public void testFire() {

        Chat chat = new Chat(42, null, null, null, null);
        Message message = new Message(0, null, chat, 0, null);
        trigger.fire(message);

        verify(clientMock).sendMessage("Помню !", 42);
    }

}