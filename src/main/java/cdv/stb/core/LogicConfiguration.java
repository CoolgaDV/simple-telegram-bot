package cdv.stb.core;

import cdv.stb.listener.MessageListenerActivator;
import cdv.stb.listener.MessageListenerTask;
import cdv.stb.plain.RememberHandler;
import cdv.stb.rates.CurrencyRateSource;
import cdv.stb.rates.CurrencyRatesHandler;
import cdv.stb.subscription.SubscriptionHandler;
import cdv.stb.subscription.SubscriptionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for application logic
 *
 * @author Dmitry Coolga
 *         23.02.2017 16:20
 */
@Configuration
public class LogicConfiguration {

    @Value("${app.polling.timeout.seconds}")
    private int pollingTimeoutSeconds;

    @Value("${app.network.failure.pause.minutes}")
    private int networkFailurePauseMinutes;

    @Value("${app.request.failure.threshold}")
    private int requestFailureThreshold;

    private final ServiceConfiguration serviceConfig;

    @Autowired
    public LogicConfiguration(ServiceConfiguration serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Bean
    public MessageListenerTask getMessageListenerTask() {
        return new MessageListenerTask(
                serviceConfig.getTelegramApiClient(),
                networkFailurePauseMinutes,
                requestFailureThreshold,
                pollingTimeoutSeconds,
                getRememberHandler(),
                getCurrencyRatesHandler(),
                getSubscriptionHandler());
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
    public RememberHandler getRememberHandler() {
        return new RememberHandler(serviceConfig.getTelegramApiClient());
    }

    @Bean
    public CurrencyRatesHandler getCurrencyRatesHandler() {
        return new CurrencyRatesHandler(
                getCurrencyRateSource(),
                serviceConfig.getTelegramApiClient());
    }

    @Bean
    public SubscriptionManager getSubscriptionManager() {
        return new SubscriptionManager(
                serviceConfig.getRedisTemplate(),
                getCurrencyRatesHandler());
    }

    @Bean
    public SubscriptionHandler getSubscriptionHandler() {
        return new SubscriptionHandler(
                serviceConfig.getTelegramApiClient(),
                getSubscriptionManager());
    }

}
