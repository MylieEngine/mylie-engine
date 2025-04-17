package mylie.engine.util;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

@Slf4j
public final class LatchUtils {
	private LatchUtils() {
		throw new IllegalInstantiationException(LatchUtils.class);
	}

	public static void await(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error("Interrupted while waiting for latch", e);
		}
	}
}
