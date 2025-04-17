package mylie.engine.core.async;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class SchedulingStrategy {
	@Getter(AccessLevel.PACKAGE)
	private final boolean multiThread;

	protected SchedulingStrategy(boolean multiThread) {
		this.multiThread = multiThread;
	}

	abstract TaskExecutor executor(Target target, Consumer<Runnable> drain);

	protected abstract WorkerThread createWorkerThread(Target target);

	protected interface TaskExecutor {
		<R> void execute(Result<R> result);

		Consumer<Runnable> drain();
	}
}
