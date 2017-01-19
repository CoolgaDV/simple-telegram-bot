package cdv.stb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring context configuration
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:08
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public MessageListenerTask getMessageListenerTask() {
        return new MessageListenerTask();
    }

    @Bean
    public MessageListenerActivator getMessageListenerActivator() {
        return new MessageListenerActivator(getMessageListenerTask());
    }

    @Bean
    public TelegramApiClient getTelegramApiClient() {
        return new TelegramApiClient("");
    }

}
