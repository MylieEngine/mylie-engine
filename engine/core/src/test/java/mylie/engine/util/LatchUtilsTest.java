package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import mylie.engine.TestUtils;
import org.junit.jupiter.api.Test;

class LatchUtilsTest {

	/**
	 * Tests the {@link LatchUtils#await(CountDownLatch)} method which is used to wait for a CountDownLatch
	 * to reach zero without propagating InterruptedException in case of an interruption.
	 */

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
	void testInstantiation() {
		TestUtils.testUtilityInstantiation(CheckedExceptions.class);
	}
}
