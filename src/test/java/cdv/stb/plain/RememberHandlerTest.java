package cdv.stb.plain;

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
public class RememberHandlerTest {

    private final TelegramApiClient clientMock = mock(TelegramApiClient.class);
    private final RememberHandler handler = new RememberHandler(clientMock);

    @Test
    public void testMatch() {
        assertTrue(handler.match(new Message(0, null, null, 0, "Помнишь")));
        assertTrue(handler.match(new Message(0, null, null, 0, "Помнишь?")));
    }

    @Test
    public void testNotMatch() {
        assertFalse(handler.match(new Message(0, null, null, 0, null)));
        assertFalse(handler.match(new Message(0, null, null, 0, "")));
        assertFalse(handler.match(new Message(0, null, null, 0, " Помнишь")));
        assertFalse(handler.match(new Message(0, null, null, 0, "ПОМНИШЬ")));
    }

    @Test
    public void testFire() {

        Chat chat = new Chat(42, null, null, null, null);
        Message message = new Message(0, null, chat, 0, null);
        handler.handle(message);

        verify(clientMock).sendMessage("Помню !", 42);
    }

}