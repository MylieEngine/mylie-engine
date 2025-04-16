package mylie.engine.core.async;

import static mylie.engine.core.async.Scheduler.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public final class SchedulingStrategies {
	private SchedulingStrategies() {
		throw new IllegalInstantiationException(SchedulingStrategies.class);
	}

	public static class SingleThread extends SchedulingStrategy {
		private final DirectExecutor directExecutor = new DirectExecutor();
		@Override
		TaskExecutor executor(Target target, Consumer<Runnable> drain) {
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

	abstract static class MultiThread extends SchedulingStrategy {
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
		TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target == Target.BACKGROUND) {
				return new ExecutorExecutor(executorService);
			} else {
				return new SubmitExecutor(target, drain);
			}
		}
	}
}
