package mylie.engine.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import mylie.engine.core.ComponentManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchedulerTest {

	@Test
	public void testInstantiation() {
		ComponentManager manager = new ComponentManager();
		Scheduler scheduler = Assertions.assertDoesNotThrow(() -> new Scheduler(manager));
		Assertions.assertNotNull(scheduler);
	}

	@Test
	public void testChangeStrategy() {
		SchedulingStrategy strategy = new SchedulingStrategies.MultiThreadExecutor(ForkJoinPool.commonPool());
		Scheduler scheduler = new Scheduler(strategy);
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> scheduler.strategy(new SchedulingStrategies.SingleThread()));
		SchedulingStrategy strategy2 = new SchedulingStrategies.MultiThreadExecutor(Executors.newFixedThreadPool(10));
		Assertions.assertDoesNotThrow(() -> scheduler.strategy(strategy2));
	}
}
