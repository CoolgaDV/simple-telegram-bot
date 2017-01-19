package cdv.stb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MessageListenerTask.class);

    @Async
    public void start() throws InterruptedException {
        Thread.sleep(1_000);
    }

}
