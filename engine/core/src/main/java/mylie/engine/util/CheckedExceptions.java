package mylie.engine.util;

import java.util.Objects;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

@Slf4j
public final class CheckedExceptions {
	private CheckedExceptions() {
		throw new IllegalInstantiationException(CheckedExceptions.class);
	}

	public static void await(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error("Interrupted while waiting for latch", e);
		}
	}

	public static <T> T poll(BlockingQueue<T> queue, long timeout, TimeUnit unit) {
		Objects.requireNonNull(queue);
		try {
			return queue.poll(timeout, unit);
		} catch (InterruptedException e) {
			log.error("Interrupted while polling queue", e);
		}
		return null;
	}

	public static void await(CyclicBarrier barrier) {
		try {
			barrier.await();
		} catch (BrokenBarrierException | InterruptedException e) {
			log.error("Interrupted while waiting for barrier", e);
		}
	}
}
