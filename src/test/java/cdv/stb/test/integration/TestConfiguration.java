package cdv.stb.test.integration;

import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.telegram.protocol.*;
import org.hamcrest.Matcher;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Spring configuration for mocking beans which depends on external systems
 *
 * @author Dmitry Coolga
 *         24.02.2017 09:04
 */
@Configuration
public class TestConfiguration {

    static final String INPUT_QUEUE_NAME = "test.input.queue";
    static final String OUTPUT_QUEUE_NAME = "test.output.queue";
    static final String SUBSCRIBER_IDS_NAME = "test.subscriber.ids";

    @Bean(INPUT_QUEUE_NAME)
    public Queue<String> getInputQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean(OUTPUT_QUEUE_NAME)
    public Queue<String> getOutputQueue() {
        return new SynchronousQueue<>();
    }

    @Bean(SUBSCRIBER_IDS_NAME)
    public Set<Long> getSubscriberIdsStorage() {
        return Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Bean
    @Primary
    public TelegramApiClient getTelegramApiClient() {
        TelegramApiClient mock = Mockito.mock(TelegramApiClient.class);
        AtomicLong updatesCounter = new AtomicLong();
        when(mock.getUpdates(anyInt(), anyLong())).then(invocation -> {
            String message = getInputQueue().poll();
            if (message == null) {
                return new Response(true, Collections.emptyList());
            }
            Chat chat = new Chat(0L, null, null, null, null);
            From from = new From(0L, null, null, "test.user");
            Message chatMessage = new Message(0L, from, chat, 0L, message);
            Result result = new Result(updatesCounter.incrementAndGet(), chatMessage);
            return new Response(true, Collections.singletonList(result));
        });
        when(mock.sendMessage(anyString(), anyLong())).then(invocation -> {
            getOutputQueue().add((String) invocation.getArguments()[0]);
            return "";
        });
        return mock;
    }

    @Bean
    @Primary
    public RestTemplate getRestTemplate() throws URISyntaxException, IOException {
        RestTemplate mock = Mockito.mock(RestTemplate.class);
        Path responsePath = Paths.get(getClass().getResource("/rates.xml").toURI());
        String responseData = Files.readAllLines(responsePath, StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining());
        Matcher<URI> matcher = new UriStartsWithMatcher("http://query.yahooapis.com");
        when(mock.getForEntity(argThat(matcher), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseData));
        return mock;
    }

    @Bean
    @Primary
    public RedisConnectionFactory getRedisConnectionFactory() {
        return mock(JedisConnectionFactory.class);
    }

    @Bean
    @Primary
    @SuppressWarnings({ "unchecked", "all" })
    public RedisTemplate<String, Long> getRedisTemplate() {

        Set<Long> subscriberIdsStorage = getSubscriberIdsStorage();

        SetOperations<String, Long> setOperationsMock = mock(SetOperations.class);
        when(setOperationsMock.members(any())).thenReturn(Collections.emptySet());
        when(setOperationsMock.add(any(), anyLong())).then(invocation -> {
            subscriberIdsStorage.add((Long) invocation.getArguments()[1]);
            return new Long(subscriberIdsStorage.size());
        });

        RedisTemplate<String, Long> redisTemplateMock = mock(RedisTemplate.class);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        return redisTemplateMock;
    }

}
