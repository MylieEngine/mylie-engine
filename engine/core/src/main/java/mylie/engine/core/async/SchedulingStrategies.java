package mylie.engine.core.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SchedulingStrategies {

	public static class SingleThread implements Scheduler.SchedulingStrategy {
		private final DirectExecutor directExecutor = new DirectExecutor();
		@Override
		public TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target.managed()) {
				return directExecutor;
			} else {
				return new SubmitExecutor(target, drain);
			}
		}

		@Override
		public boolean multiThread() {
			return false;
		}
	}

	abstract static class MultiThread implements Scheduler.SchedulingStrategy {
		@Override
		public boolean multiThread() {
			return true;
		}

		static class ExecutorExecutor implements TaskExecutor {
			private final ExecutorService executor;

			public ExecutorExecutor(ExecutorService executor) {
				this.executor = executor;
			}

			@Override
			public <R> void execute(Result<R> result) {
				CompletableFuture.supplyAsync(result::execute, executor);
			}
		}
	}

	public static class MultiThreadExecutor extends MultiThread {
		private final ExecutorService executorService;

		public MultiThreadExecutor(ExecutorService executorService) {
			this.executorService = executorService;
		}

		@Override
		public TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target == Target.BACKGROUND) {
				return new ExecutorExecutor(executorService);
			} else {
				return new SubmitExecutor(target, drain);
			}
		}
	}
}
