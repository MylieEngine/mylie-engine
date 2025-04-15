package mylie.engine.core.async;

import static mylie.engine.core.async.AsyncTestData.INTEGER_ADD;
import static mylie.engine.core.async.AsyncTestData.SCHEDULER_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AsyncTest {

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testInit(Scheduler scheduler) {
		scheduler.multiThreaded();
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> scheduler.register(Cache.NO));
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> scheduler.unregister(Cache.NO));
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
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
	@MethodSource(SCHEDULER_SOURCE)
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
	@MethodSource(SCHEDULER_SOURCE)
	public void testNoTarget(Scheduler scheduler) {
		Target target = new Target("test1", false);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		assertDoesNotThrow(() -> scheduler.register(Cache.NO));
		assertThrows(IllegalArgumentException.class, () -> Async.async(scheduler, ExecutionMode.DIRECT, target,
				Cache.NO, 0, AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger));
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testDoubleTarget(Scheduler scheduler) {
		Target target = new Target("test2", false);
		Queue<Runnable> taskQueue = new LinkedList<>();
		assertDoesNotThrow(() -> scheduler.register(target, taskQueue::add));
		assertThrows(IllegalArgumentException.class, () -> scheduler.register(target, taskQueue::add));
		assertDoesNotThrow(() -> scheduler.unregister(target));
		assertThrows(IllegalArgumentException.class, () -> scheduler.unregister(target));
		assertEquals(0, taskQueue.size());
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testNotManagedTarget(Scheduler scheduler) {
		Target target = new Target("test3", false);
		Queue<Runnable> taskQueue = new LinkedList<>();
		AtomicInteger atomicInteger = new AtomicInteger(0);
		scheduler.register(Cache.NO);
		scheduler.register(target, taskQueue::add);
		assertEquals(0, taskQueue.size());
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.DIRECT, target, Cache.NO, 0,
				AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger);
		assertEquals(1, taskQueue.size());
		if (!taskQueue.isEmpty()) {
			taskQueue.poll().run();
		}
		assertTrue(result.get());
		assertEquals(1, atomicInteger.get());
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
		assertDoesNotThrow(() -> scheduler.unregister(target));
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testOneFrameCache(Scheduler scheduler) {
		Target target = new Target("test4", false);
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

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testSelfLocking(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.NO, 0,
				AsyncTestData.SELF_LOCKING, scheduler, Cache.NO, atomicInteger);
		result.get();
		assertEquals(2, atomicInteger.get());
		scheduler.unregister(Cache.NO);
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testException(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		Result<Boolean> result = Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.NO, 0,
				AsyncTestData.THROWS_ILLEGAL_STATE_EXCEPTION);
		Exception exception = assertThrows(Exception.class, result::get);
		IllegalStateException illegalStateException = assertInstanceOf(IllegalStateException.class,
				exception.getCause());
		assertEquals("Test exception", illegalStateException.getMessage());
		scheduler.unregister(Cache.NO);
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testAdd(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		int a = 10;
		int b = 5;
		Result<Integer> async = Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.NO, 0, INTEGER_ADD,
				a, b);
		assertEquals(15, async.get());
		scheduler.unregister(Cache.NO);
	}

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testOneFrameLock(Scheduler scheduler) {
		scheduler.register(Cache.ONE_FRAME);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		List<Result<Boolean>> results = new CopyOnWriteArrayList<>();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		final int THREADS = 100;
		CountDownLatch latch = new CountDownLatch(THREADS);
		for (int i = 0; i < THREADS; i++) {
			new Thread(() -> {
				try {
					countDownLatch.await();
					results.add(Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0,
							AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger));
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
			// results.add(Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND,
			// Cache.ONE_FRAME, 0, AsyncTestData.ATOMIC_INT_INCREASE, atomicInteger));
		}

		countDownLatch.countDown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(THREADS, results.size());
		for (Result<Boolean> result : results) {
			assertTrue(result.get());
		}
		assertEquals(1, atomicInteger.get());
		scheduler.unregister(Cache.ONE_FRAME);
	}

}
