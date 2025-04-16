package mylie.engine.core.async;

import java.util.function.Consumer;

public abstract class SchedulingStrategy {
	abstract TaskExecutor executor(Target target, Consumer<Runnable> drain);

	protected abstract boolean multiThread();

	protected interface TaskExecutor {
		<R> void execute(Result<R> result);
	}
}
