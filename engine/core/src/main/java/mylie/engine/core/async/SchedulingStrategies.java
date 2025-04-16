package mylie.engine.core.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import lombok.Getter;
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

	static class SubmitExecutor implements SchedulingStrategy.TaskExecutor {
		@Getter
		private final Target target;
		private final Consumer<Runnable> drain;

		public SubmitExecutor(Target target, Consumer<Runnable> drain) {
			this.target = target;
			this.drain = drain;
		}

		@Override
		public <R> void execute(Result<R> result) {
			drain.accept(() -> Async.executeTask(result));
		}
	}

	static class DirectExecutor implements SchedulingStrategy.TaskExecutor {
		@Override
		public <R> void execute(Result<R> result) {
			Async.executeTask(result);
		}
	}
}
