package cdv.stb;

import cdv.stb.protocol.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author Dmitry Coolga
 *         21.01.2017 11:12
 */
public class MessageListenerTaskTest {

    private static final long CHAT_ID = 1L;

    private final ObjectMapper mapper = new ObjectMapper();

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
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(first))
                .thenReturn(serialize(second))
                .thenReturn(serialize(third))
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
    public void testNonMatchedMessage() {

        Response response = new Response(
                true,
                Collections.singletonList(createResult(100, "foo")));

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(response))
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(3)).getUpdates(anyInt(), anyLong());
        verify(clientMock, never()).sendMessage(any(), anyLong());
    }

    @Test
    public void testMatchedMessage() {

        Response response = new Response(
                true,
                Collections.singletonList(createResult(100, "Помнишь ")));

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(response))
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(3)).getUpdates(anyInt(), anyLong());
        verify(clientMock).sendMessage("Помню !", CHAT_ID);
    }

    @Test
    public void testRequestFailure() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(new Response(false, new ArrayList<>())));

        startTask();

        verify(clientMock, times(4)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testRequestFailureRecovery() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(new Response(false, new ArrayList<>())))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn(serialize(new Response(false, new ArrayList<>())));

        startTask();

        verify(clientMock, times(6)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testMessageFormatException() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenReturn("foo");

        startTask();

        verify(clientMock, times(2)).getUpdates(anyInt(), anyLong());
    }

    @Test
    public void testUnexpectedException() {

        when(clientMock.getUpdates(anyInt(), anyInt()))
                .thenReturn(serialize(new Response(true, new ArrayList<>())))
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(2)).getUpdates(anyInt(), anyLong());
    }

    private Result createResult(long updateId, String text) {
        return new Result(updateId, new Message(
                1,
                new From(1, "", "", ""),
                new Chat(CHAT_ID, "", "", "", ""),
                1,
                text));
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
                .thenReturn(serialize(response))
                .thenThrow(terminate());

        startTask();

        verify(clientMock, times(updatesCall)).getUpdates(
                timeoutCaptor.capture(),
                offsetCaptor.capture());
    }

    private void startTask() {
        new MessageListenerTask(clientMock, 1, 2, 5).start();
    }

    private String serialize(Response response) {
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error while serializing response", ex);
        }
    }

    private RuntimeException terminate() {
        return new RuntimeException("Termination");
    }

}