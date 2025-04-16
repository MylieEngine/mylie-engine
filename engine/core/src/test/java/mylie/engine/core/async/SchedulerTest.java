package mylie.engine.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchedulerTest {
	@Test
	public void testChangeStrategy() {
		Scheduler.SchedulingStrategy strategy = new SchedulingStrategies.MultiThreadExecutor(ForkJoinPool.commonPool());
		Scheduler scheduler = new Scheduler(strategy);
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> scheduler.strategy(new SchedulingStrategies.SingleThread()));
		Scheduler.SchedulingStrategy strategy2 = new SchedulingStrategies.MultiThreadExecutor(
				Executors.newFixedThreadPool(10));
		Assertions.assertDoesNotThrow(() -> scheduler.strategy(strategy2));
	}
}
