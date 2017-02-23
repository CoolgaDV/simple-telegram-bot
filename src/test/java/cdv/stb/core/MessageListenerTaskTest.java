package cdv.stb.core;

import cdv.stb.common.MessageHandler;
import cdv.stb.exception.RequestFailureException;
import cdv.stb.listener.MessageListenerTask;
import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.telegram.protocol.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Dmitry Coolga
 *         21.01.2017 11:12
 */
public class MessageListenerTaskTest {

    private static final long CHAT_ID = 1L;

    private final TelegramApiClient clientMock = mock(TelegramApiClient.class);

    private final ArgumentCaptor<Integer> timeoutCaptor =
            ArgumentCaptor.forClass(Integer.class);
    private final ArgumentCaptor<Long> offsetCaptor =
            ArgumentCaptor.forClass(Long.class);

    @Test
    public void testUpdateIdChanges() {

        Response first = new Response(true,
                Arrays.asList(createResult(100, ""), createResult(90, "")));
        Response second = new Response(true,
                Collections.singletonList(createResult(80, "")));
        Response third = new Response(true,
                Collections.singletonList(createResult(110, "")));

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenReturn(first)
                .thenReturn(second)
                .thenReturn(third)
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(5)).getUpdates(anyInt(), offsetCaptor.capture());

        List<Long> offsets = offsetCaptor.getAllValues();
        Assert.assertEquals(0, (long) offsets.get(0));
        Assert.assertEquals(1, (long) offsets.get(1));
        Assert.assertEquals(101, (long) offsets.get(2));
        Assert.assertEquals(101, (long) offsets.get(3));
        Assert.assertEquals(111, (long) offsets.get(4));
    }

    @Test
    public void testHandlers() {

        Response response = new Response(
                true,
                Collections.singletonList(createResult(100, "foo")));

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenReturn(response)
                .thenThrow(terminate());

        MessageHandler unmatchedHandler = mock(MessageHandler.class);
        when(unmatchedHandler.match(any())).thenReturn(false);

        MessageHandler matchedHandler = mock(MessageHandler.class);
        when(matchedHandler.match(any())).thenReturn(true);

        startTask(unmatchedHandler, matchedHandler);

        verify(clientMock, times(3)).getUpdates(anyInt(), anyLong());
        verify(unmatchedHandler).match(any());
        verify(unmatchedHandler, never()).handle(any());
        verify(matchedHandler).match(any());
        verify(matchedHandler).handle(any());
    }

    @Test
    public void testRequestFailure() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenThrow(new RequestFailureException(""));

        startTask();

        verify(clientMock, times(4)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testRequestFailureRecovery() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenThrow(new RequestFailureException(""))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenThrow(new RequestFailureException(""));

        startTask();

        verify(clientMock, times(6)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testUnexpectedException() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(new Response(true, new ArrayList<>()))
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(2)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testNoStaleUpdates() {

        run(new Response(true, new ArrayList<>()), 2);

        assertEquals(5, (int) timeoutCaptor.getValue());
        assertEquals(1, (long) offsetCaptor.getValue());
    }

    @Test
    public void testFewStaleUpdates() {

        Response response = new Response(true, Arrays.asList(
                new Result(50, null),
                new Result(100, null)));

        run(response, 2);

        assertEquals(5, (int) timeoutCaptor.getValue());
        assertEquals(101, (long) offsetCaptor.getValue());
    }

    @Test
    public void testReadStaleUpdatesFailure() {

        run(new Response(false, null), 1);

        assertEquals(0, (int) timeoutCaptor.getValue());
        assertEquals(0, (long) offsetCaptor.getValue());
    }

    private void run(Response response, int updatesCall) {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(response)
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(updatesCall)).getUpdates(
                timeoutCaptor.capture(),
                offsetCaptor.capture());
    }

    private void startTask(MessageHandler... handlers) {
        new MessageListenerTask(clientMock, 1, 2, 5, handlers).start();
    }

    private RuntimeException terminate() {
        return new RuntimeException("Termination");
    }

    private Result createResult(long updateId, String text) {
        return new Result(updateId, new Message(
                1,
                new From(1, "", "", ""),
                new Chat(CHAT_ID, "", "", "", ""),
                1,
                text));
    }

}