package mylie.engine.core.async;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
@Getter(AccessLevel.PROTECTED)
public abstract class WorkerThread {

	private final Target target;

	protected WorkerThread(Target target) {
		this.target = target;
	}

	protected abstract Consumer<Runnable> drain();

	public abstract void stop();
}
