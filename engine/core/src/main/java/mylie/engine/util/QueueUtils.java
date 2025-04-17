package mylie.engine.util;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

@Slf4j
public final class QueueUtils {
	private QueueUtils() {
		throw new IllegalInstantiationException(QueueUtils.class);
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
}
