package cdv.stb.core;

import cdv.stb.telegram.TelegramApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration for auxiliary service beans
 *
 * @author Dmitry Coolga
 *         23.02.2017 16:20
 */
@Configuration
public class ServiceConfiguration {

    @Value("${app.bot.token}")
    private String botToken;

    @Value("${app.redis.host}")
    private String redisHost;

    @Value("${app.redis.port}")
    private int redisPort;

    @Value("${app.redis.db.index}")
    private int redisDbIndex;

    @Bean
    public TelegramApiClient getTelegramApiClient() {
        return new TelegramApiClient(botToken, getRestTemplate());
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisConnectionFactory getRedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setDatabase(redisDbIndex);
        return factory;
    }

    @Bean
    public RedisTemplate<String, Long> getRedisTemplate() {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(getRedisConnectionFactory());
        template.setKeySerializer(serializer);
        template.setHashKeySerializer(serializer);
        return template;
    }

}
