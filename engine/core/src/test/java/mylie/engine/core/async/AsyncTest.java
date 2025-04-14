package mylie.engine.core.async;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AsyncTest {

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testInit(Scheduler scheduler) {
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> scheduler.register(Cache.NO));
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> scheduler.unregister(Cache.NO));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testAsync(Scheduler scheduler) {
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.NO, 0,
				AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger);
		assertTrue(result.get());
		assertEquals(1, atomicInteger.get());
		result = Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.NO, 0,
				AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger);
		assertTrue(result.get());
		assertEquals(2, atomicInteger.get());
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testDirectExecution(Scheduler scheduler) {
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.DIRECT, Target.BACKGROUND, Cache.NO, 0,
				AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger);
		assertTrue(result.get());
		assertEquals(1, atomicInteger.get());
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testNoTarget(Scheduler scheduler) {
		Target target = new Target("test", false);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> Async.async(scheduler, ExecutionMode.DIRECT, target,
				Cache.NO, 0, AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testDoubleTarget(Scheduler scheduler) {
		Target target = new Target("test", false);
		Queue<Runnable> taskQueue = new LinkedList<>();
		assertDoesNotThrow(() -> scheduler.register(target, taskQueue::add));
		assertThrows(IllegalArgumentException.class, () -> scheduler.register(target, taskQueue::add));
		assertDoesNotThrow(() -> scheduler.unregister(target));
		assertThrows(IllegalArgumentException.class, () -> scheduler.unregister(target));
		assertEquals(0, taskQueue.size());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testNotManagedTarget(Scheduler scheduler) {
		Target target = new Target("test", false);
		Queue<Runnable> taskQueue = new LinkedList<>();
		AtomicInteger atomicInteger = new AtomicInteger(0);
		scheduler.register(Cache.NO);
		scheduler.register(target, taskQueue::add);
		assertEquals(0, taskQueue.size());
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.NO, 0,
				AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger);
		assertEquals(1, taskQueue.size());
		if(!taskQueue.isEmpty()){
			taskQueue.poll().run();
		}
		assertTrue(result.get());
		assertEquals(1, atomicInteger.get());
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
		assertDoesNotThrow(() -> scheduler.unregister(target));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.core.async.AsyncTestData#schedulerProvider")
	public void testOneFrameCache(Scheduler scheduler) {
		Target target = new Target("test", false);
		Queue<Runnable> taskQueue = new LinkedList<>();
		AtomicInteger atomicInteger = new AtomicInteger(0);
		scheduler.register(Cache.ONE_FRAME);
		scheduler.register(Cache.NO);
		scheduler.register(target, taskQueue::add);

		assertEquals(0, taskQueue.size());
		Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.ONE_FRAME, 0, AsyncTestData.ATOMIC_INT_INCREASE,
				atomicInteger);
		Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.ONE_FRAME, 0, AsyncTestData.ATOMIC_INT_INCREASE,
				atomicInteger);
		assertEquals(1, taskQueue.size());
		scheduler.onUpdate();
		Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.ONE_FRAME, 0, AsyncTestData.ATOMIC_INT_INCREASE,
				atomicInteger);
		Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.ONE_FRAME, 0, AsyncTestData.ATOMIC_INT_INCREASE,
				atomicInteger);
		assertEquals(2, taskQueue.size());
		while (!taskQueue.isEmpty()) {
			taskQueue.poll().run();
		}
		assertEquals(2, atomicInteger.get());
		scheduler.unregister(Cache.ONE_FRAME);
		scheduler.unregister(target);
	}
}
