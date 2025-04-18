package mylie.engine.core.async;

import static mylie.engine.core.async.AsyncTestData.SCHEDULER_SOURCE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestWorkerThread {
	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testWorkerThreadExecution(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		Target target = new Target("TestWorkerThread");
		WorkerThread workerThread = scheduler.createWorkerThread(target);
		Assertions.assertNotNull(workerThread);
		Result<String> async = Async.async(scheduler, ExecutionMode.ASYNC, target, Cache.NO, 0, GET_THREAD_NAME);
		if (scheduler.multiThreaded()) {
			Assertions.assertEquals(target.name(), async.get());
		}
		scheduler.unregister(Cache.NO);
		Assertions.assertDoesNotThrow(workerThread::stop);
	}

	private static final Functions.Zero<String> GET_THREAD_NAME = new Functions.Zero<>("GET_THREAD_NAME") {
		@Override
		protected String execute() {
			return Thread.currentThread().getName();
		}
	};
}
