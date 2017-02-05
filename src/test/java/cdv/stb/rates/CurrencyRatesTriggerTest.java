package cdv.stb.rates;

import cdv.stb.TelegramApiClient;
import cdv.stb.protocol.Chat;
import cdv.stb.protocol.Message;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Coolga
 *         05.02.2017 11:20
 */
public class CurrencyRatesTriggerTest {

    private final TelegramApiClient clientMock = mock(TelegramApiClient.class);
    private final CurrencyRateSource rateSourceMock = mock(CurrencyRateSource.class);
    
    @Test
    public void testMatch() {
        CurrencyRatesTrigger trigger = new CurrencyRatesTrigger(rateSourceMock, clientMock);
        assertTrue(trigger.match(new Message(0, null, null, 0, "Курс валют")));
    }

    @Test
    public void testNotMatch() {
        CurrencyRatesTrigger trigger = new CurrencyRatesTrigger(rateSourceMock, clientMock);
        assertFalse(trigger.match(new Message(0, null, null, 0, null)));
        assertFalse(trigger.match(new Message(0, null, null, 0, "")));
        assertFalse(trigger.match(new Message(0, null, null, 0, "Курс валют !")));
        assertFalse(trigger.match(new Message(0, null, null, 0, "КУРС ВАЛЮТ")));
    }

    @Test
    public void testFireWithoutAvailableData() {

        when(rateSourceMock.getCurrentRates()).thenReturn(Collections.emptyList());

        CurrencyRatesTrigger trigger = new CurrencyRatesTrigger(rateSourceMock, clientMock);
        Chat chat = new Chat(42, null, null, null, null);
        Message message = new Message(0, null, chat, 0, null);
        trigger.fire(message);

        verify(clientMock).sendMessage("Данные по курсам валют недоступны...", 42);
    }

    @Test
    public void testFireWithAvailableData() {

        when(rateSourceMock.getCurrentRates()).thenReturn(Arrays.asList(
                new CurrencyRate("USDRUB", "1.0", null, null),
                new CurrencyRate("EURRUB", "2.0", null, null)));

        CurrencyRatesTrigger trigger = new CurrencyRatesTrigger(rateSourceMock, clientMock);
        Chat chat = new Chat(42, null, null, null, null);
        Message message = new Message(0, null, chat, 0, null);
        trigger.fire(message);

        verify(clientMock).sendMessage("Доллар: 1.0\nЕвро: 2.0", 42);
    }

}