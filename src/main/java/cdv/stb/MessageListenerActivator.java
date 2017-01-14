package cdv.stb;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application event listener for launching {@link MessageListenerTask}
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:38
 */
@Component
public class MessageListenerActivator {

    private final MessageListenerTask task;

    public MessageListenerActivator(MessageListenerTask task) {
        this.task = task;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void onContextRefreshed() throws InterruptedException {
        task.start();
    }

}
