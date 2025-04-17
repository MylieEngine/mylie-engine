package mylie.engine.core.async;

import static mylie.engine.core.async.AsyncTestData.SCHEDULER_SOURCE;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import mylie.engine.core.ComponentManager;
import mylie.engine.util.LatchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testSubmission(Scheduler scheduler) {
		Queue<Runnable> queue = new LinkedList<>();
		Target target = new Target("TestSubmission", true, false);
		scheduler.register(target, queue::add);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		scheduler.submit(atomicInteger::incrementAndGet, target);
		if (scheduler.multiThreaded()) {
			Assertions.assertEquals(1, queue.size());
		} else {
			Assertions.assertEquals(0, queue.size());
			Assertions.assertEquals(1, atomicInteger.get());
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		AtomicInteger atomicInteger2 = new AtomicInteger(0);
		scheduler.submit(() -> {
			atomicInteger2.incrementAndGet();
			countDownLatch.countDown();
		}, Target.BACKGROUND);
		LatchUtils.await(countDownLatch);
		Assertions.assertEquals(1, atomicInteger2.get());
		scheduler.unregister(target);

		Target target2 = new Target("TestSubmission2", true, false);
		Assertions.assertThrows(IllegalArgumentException.class, () -> scheduler.submit(null, target2));
	}
}
