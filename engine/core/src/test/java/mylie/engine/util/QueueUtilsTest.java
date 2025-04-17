package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import mylie.engine.TestUtils;
import org.junit.jupiter.api.Test;

class QueueUtilsTest {

    @Test
    void testPollReturnsElementWhenAvailable() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        queue.add("TestElement");
        String result = QueueUtils.poll(queue, 1000L, TimeUnit.MILLISECONDS);
        assertEquals("TestElement", result);
    }

    @Test
    void testPollReturnsNullWhenQueueIsEmpty() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        String result = QueueUtils.poll(queue, 1000L, TimeUnit.MILLISECONDS);
        assertNull(result);
    }

    @Test
    void testPollReturnsNullOnInterruptedException() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        final String[] result = new String[1];
        Thread pollThread = new Thread(() -> result[0] = QueueUtils.poll(queue, 5000L, TimeUnit.MILLISECONDS));
        pollThread.start();
        Thread.sleep(100);
        pollThread.interrupt();
        pollThread.join(1000);
        assertNull(result[0]);
        assertFalse(pollThread.isAlive());
    }

    @Test
    void testPollHandlesNullQueueGracefully() {
        assertThrows(NullPointerException.class,()->QueueUtils.poll(null, 1000L, TimeUnit.MILLISECONDS));
    }

    @Test
    void testInstantiation() {
        TestUtils.testUtilityInstantiation(QueueUtils.class);
    }
}