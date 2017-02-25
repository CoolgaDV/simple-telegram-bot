package cdv.stb.test.integration;

import cdv.stb.core.LogicConfiguration;
import cdv.stb.core.ServiceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests isolated from external dependencies (such as Redis, Telegram server, etc).
 *
 * @author Dmitry Coolga
 *         24.02.2017 09:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        LogicConfiguration.class,
        ServiceConfiguration.class,
        TestConfiguration.class
})
public class IntegrationTest {

    @Autowired
    @SuppressWarnings("all")
    private ApplicationContext context;

    private Queue<String> inputQueue;
    private BlockingQueue<String> outputQueue;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        inputQueue = (Queue<String>) context.getBean(TestConfiguration.INPUT_QUEUE_NAME);
        outputQueue = (BlockingQueue<String>) context.getBean(TestConfiguration.OUTPUT_QUEUE_NAME);
    }

    @Test
    public void testRemember() throws InterruptedException {
        inputQueue.add("Помнишь");
        assertEquals("Помню !", outputQueue.poll(1, TimeUnit.MINUTES));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubscribe() throws InterruptedException {
        inputQueue.add("Бот, подписка");
        assertEquals("Подписка успешно зарегистрирована", outputQueue.poll(1, TimeUnit.MINUTES));
        Set<Long> subscriberIds = (Set<Long>) context.getBean(TestConfiguration.SUBSCRIBER_IDS_NAME);
        assertEquals(1, subscriberIds.size());
        assertTrue(subscriberIds.contains(0L));
    }

}
