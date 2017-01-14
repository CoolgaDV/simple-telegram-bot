package cdv.stb;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Telegram message listening routine
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:11
 */
@Component
public class MessageListenerTask {

    @Async
    public void start() throws InterruptedException {
        Thread.sleep(1_000);
    }

}
