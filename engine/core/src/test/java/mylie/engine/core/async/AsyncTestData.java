package mylie.engine.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

final class AsyncTestData {
	private AsyncTestData() {
	}
	public static final String SCHEDULER_SOURCE = "mylie.engine.core.async.AsyncTestData#schedulerProvider";
	public static Stream<Scheduler> schedulerProvider() {
		return Stream.of(new Scheduler(new SchedulingStrategies.SingleThread()),
				new Scheduler(new SchedulingStrategies.MultiThreadExecutor(ForkJoinPool.commonPool())),
				new Scheduler(new SchedulingStrategies.MultiThreadExecutor(Executors.newSingleThreadExecutor())),
				new Scheduler(
						new SchedulingStrategies.MultiThreadExecutor(Executors.newVirtualThreadPerTaskExecutor())),
				new Scheduler(new SchedulingStrategies.MultiThreadExecutor(Executors.newFixedThreadPool(4))));
	}

	public static final Functions.One<AtomicInteger, Boolean> ATOMIC_INT_INCREASE = new Functions.One<>(
			"ATOMIC_INT_INCREASE") {
		@Override
		protected Boolean execute(AtomicInteger param0) {
			param0.incrementAndGet();
			return true;
		}
	};

	public static final Functions.One<AtomicInteger, Boolean> ATOMIC_INT_DECREASE = new Functions.One<>(
			"ATOMIC_INT_DECREASE") {
		@Override
		protected Boolean execute(AtomicInteger param0) {
			param0.decrementAndGet();
			return true;
		}
	};

	public static final Functions.Three<Scheduler, Cache, AtomicInteger, Boolean> SELF_LOCKING = new Functions.Three<>(
			"SELF_LOCKING") {

		@Override
		protected Boolean execute(Scheduler param0, Cache param2, AtomicInteger param3) {
			int i = param3.incrementAndGet();
			if (i < 2) {
				Result<Boolean> result = Async.async(param0, ExecutionMode.ASYNC, Target.BACKGROUND, param2, 0,
						SELF_LOCKING, param0, param2, param3);
				result.get();
			}
			return true;
		}
	};
}
