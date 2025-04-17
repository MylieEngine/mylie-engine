package mylie.engine.core.async;

import static mylie.engine.core.async.AsyncTestData.SCHEDULER_SOURCE;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TaskTest {

	@ParameterizedTest
	@MethodSource(SCHEDULER_SOURCE)
	public void testExecutionOrder(Scheduler scheduler) {
		scheduler.register(Cache.ONE_FRAME);
		List<String> list = new SaveHashList<>();
		String taskName1 = "task1";
		String taskName2 = "task2";
		String taskName3 = "task3";
		String taskName4 = "task4";
		Task<Boolean> task1 = new AddStringTask(scheduler, list, taskName1);
		Task<Boolean> task2 = new AddStringTask(scheduler, list, taskName2);
		Task<Boolean> task3 = new AddStringTask(scheduler, list, taskName3);
		Task<Boolean> task4 = new AddStringTask(scheduler, list, taskName4);
		task1.dependencies().add(task2);
		task1.dependencies().add(task4);
		task2.dependencies().add(task3);
		task3.dependencies().add(task4);
		task1.execute().get();
		Assertions.assertEquals(4, list.size());
		Assertions.assertTrue(list.indexOf(taskName2) < list.indexOf(taskName1));
		Assertions.assertTrue(list.indexOf(taskName4) < list.indexOf(taskName3));
		Assertions.assertTrue(list.indexOf(taskName4) < list.indexOf(taskName2));
		Assertions.assertTrue(list.indexOf(taskName4) < list.indexOf(taskName1));
		scheduler.unregister(Cache.ONE_FRAME);
	}

	private static class AddStringTask extends Task<Boolean> {
		private final Scheduler scheduler;
		private final List<String> resultList;
		private final String id;

		public AddStringTask(Scheduler scheduler, List<String> resultList, String id) {
			this.scheduler = scheduler;
			this.resultList = resultList;
			this.id = id;
		}

		@Override
		protected Result<Boolean> executeTask() {
			return Async.async(scheduler, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, ADD_STRING,
					resultList, id);
		}

		private static final Functions.Two<List<String>, String, Boolean> ADD_STRING = new Functions.Two<>(
				"ADD_STRING") {
			@Override
			protected Boolean execute(List<String> param0, String param1) {
				param0.add(param1);
				return true;
			}
		};
	}

	private static class SaveHashList<T> extends ArrayList<T> {
		@Serial
		private static final long serialVersionUID = -3155051513704092918L;
		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
	}
}
