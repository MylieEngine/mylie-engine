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
	}

	abstract static class MultiThread implements Scheduler.SchedulingStrategy {

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
		private final ExecutorService executor;

		public MultiThreadExecutor(ExecutorService executor) {
			this.executor = executor;
		}

		@Override
		public TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target == Target.BACKGROUND) {
				return new ExecutorExecutor(executor);
			} else {
				return new SubmitExecutor(target, drain);
			}
		}
	}
}
