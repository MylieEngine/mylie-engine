package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.*;
import mylie.engine.TestUtils;
import org.junit.jupiter.api.Test;

public class CheckedExceptionsTest {

	@Test
	void testPollReturnsElementWhenAvailable() {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
		queue.add("TestElement");
		String result = CheckedExceptions.poll(queue, 1000L, TimeUnit.MILLISECONDS);
		assertEquals("TestElement", result);
	}

	@Test
	void testPollReturnsNullWhenQueueIsEmpty() {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
		String result = CheckedExceptions.poll(queue, 1000L, TimeUnit.MILLISECONDS);
		assertNull(result);
	}

	@Test
	void testPollReturnsNullOnInterruptedException() throws InterruptedException {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
		final String[] result = new String[1];
		Thread pollThread = new Thread(() -> result[0] = CheckedExceptions.poll(queue, 5000L, TimeUnit.MILLISECONDS));
		pollThread.start();
		Thread.sleep(100);
		pollThread.interrupt();
		pollThread.join(1000);
		assertNull(result[0]);
		assertFalse(pollThread.isAlive());
	}

	@Test
	void testPollHandlesNullQueueGracefully() {
		assertThrows(NullPointerException.class, () -> CheckedExceptions.poll(null, 1000L, TimeUnit.MILLISECONDS));
	}

	@Test
	void testAwaitLatchReachesZero() {
		CountDownLatch latch = new CountDownLatch(1);

		new Thread(latch::countDown).start();

		assertDoesNotThrow(() -> CheckedExceptions.await(latch), "LatchUtils.await should not throw an exception");
		assertEquals(0, latch.getCount(), "Latch count should reach zero after awaiting");
	}

	@Test
	void testAwaitHandlesInterruptedException() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		final boolean[] threadCompleted = new boolean[1];
		Thread awaitingThread = new Thread(() -> {
			CheckedExceptions.await(latch);
			threadCompleted[0] = true;
		});
		awaitingThread.start();
		Thread.sleep(100);
		assertTrue(awaitingThread.isAlive(), "Thread should be alive and waiting on latch");
		assertEquals(1, latch.getCount(), "Latch count should still be 1");
		awaitingThread.interrupt();
		awaitingThread.join(1000);
		assertFalse(awaitingThread.isAlive(), "Thread should no longer be alive after interruption");
		assertTrue(threadCompleted[0], "Thread should have completed execution after interruption");
		assertEquals(1, latch.getCount(), "Latch count should still be 1 after interruption");

	}

	@Test
	void testAwaitOnAlreadyZeroedLatch() {
		CountDownLatch latch = new CountDownLatch(0);

		assertDoesNotThrow(() -> CheckedExceptions.await(latch),
				"LatchUtils.await should not throw an exception for a latch with count zero");
		assertEquals(0, latch.getCount(), "Latch count should remain zero after awaiting on a zeroed latch");
	}

	@Test
	void testCyclicBarrierInterruption() throws InterruptedException {
		CyclicBarrier barrier = new CyclicBarrier(2);
		final boolean[] threadCompleted = new boolean[1];
		Thread awaitingThread = new Thread(() -> {
			CheckedExceptions.await(barrier);
			threadCompleted[0] = true;
		});
		awaitingThread.start();
		Thread.sleep(100);
		assertTrue(awaitingThread.isAlive());
		assertEquals(1, barrier.getNumberWaiting());
		awaitingThread.interrupt();
		awaitingThread.join(1000);
		assertFalse(awaitingThread.isAlive());
		assertTrue(threadCompleted[0]);
		assertEquals(0, barrier.getNumberWaiting());
	}

	@Test
	void testInstantiation() {
		TestUtils.testUtilityInstantiation(CheckedExceptions.class);
	}
}
