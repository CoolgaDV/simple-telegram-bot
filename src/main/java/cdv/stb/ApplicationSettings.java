package cdv.stb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dmitry Coolga
 *         21.01.2017 08:53
 */
@Configuration
public class ApplicationSettings {

    @Value("${app.bot.token}")
    private String botToken;

    @Value("${app.polling.timeout.seconds}")
    private int pollingTimeoutSeconds;

    @Value("${app.network.failure.pause.minutes}")
    private int networkFailurePauseMinutes;

    @Value("${app.request.failure.threshold}")
    private int requestFailureThreshold;

    public String getBotToken() {
        return botToken;
    }

    public int getPollingTimeoutSeconds() {
        return pollingTimeoutSeconds;
    }

    public int getNetworkFailurePauseMinutes() {
        return networkFailurePauseMinutes;
    }

    public int getRequestFailureThreshold() {
        return requestFailureThreshold;
    }

}
