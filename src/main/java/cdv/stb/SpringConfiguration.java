package cdv.stb;

import cdv.stb.rates.CurrencyRateSource;
import cdv.stb.rates.CurrencyRatesTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Spring context configuration
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:08
 */
@Configuration
@Import(ApplicationSettings.class)
public class SpringConfiguration {

    @Autowired
    private ApplicationSettings applicationSettings;

    @Bean
    public MessageListenerTask getMessageListenerTask() {
        return new MessageListenerTask(
                getTelegramApiClient(),
                applicationSettings.getNetworkFailurePauseMinutes(),
                applicationSettings.getRequestFailureThreshold(),
                applicationSettings.getPollingTimeoutSeconds(),
                getRememberTrigger(),
                getCurrencyRatesTrigger(),
                getSubscriptionTrigger());
    }

    @Bean
    public MessageListenerActivator getMessageListenerActivator() {
        return new MessageListenerActivator(getMessageListenerTask());
    }

    @Bean
    public CurrencyRateSource getCurrencyRateSource() {
        return new CurrencyRateSource();
    }

    @Bean
    public TelegramApiClient getTelegramApiClient() {
        return new TelegramApiClient(applicationSettings.getBotToken());
    }

    @Bean
    public RememberTrigger getRememberTrigger() {
        return new RememberTrigger(getTelegramApiClient());
    }

    @Bean
    public CurrencyRatesTrigger getCurrencyRatesTrigger() {
        return new CurrencyRatesTrigger(getCurrencyRateSource(), getTelegramApiClient());
    }

    @Bean
    public RedisConnectionFactory getRedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(applicationSettings.getRedisHost());
        factory.setPort(applicationSettings.getRedisPort());
        factory.setDatabase(applicationSettings.getRedisDbIndex());
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

    @Bean
    public SubscriptionManager getSubscriptionManager() {
        return new SubscriptionManager(
                getRedisTemplate(),
                getCurrencyRatesTrigger());
    }

    @Bean
    public SubscriptionTrigger getSubscriptionTrigger() {
        return new SubscriptionTrigger(getTelegramApiClient(), getSubscriptionManager());
    }

}
