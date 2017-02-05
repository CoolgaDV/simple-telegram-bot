package cdv.stb;

import cdv.stb.rates.CurrencyRateSource;
import cdv.stb.rates.CurrencyRatesTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
                getCurrencyRatesTrigger());
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

}
