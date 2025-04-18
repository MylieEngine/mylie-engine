package mylie.engine.core.async;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Target(String name, boolean managed, boolean bindable) {
	public static final Target BACKGROUND = new Target("Background", true, false);
	private static final ThreadLocal<Target> THREAD_LOCAL = new ThreadLocal<>();
	public Target(String name) {
		this(name, false, true);
	}

	public Target(String name, boolean managed) {
		this(name, managed, true);
	}

	public void bind() {
		if (!bindable) {
			log.warn("Target < {} > is not bindable", name);
			throw new IllegalStateException("Target < " + name + " > is not bindable");
		}
		if (THREAD_LOCAL.get() != null) {
			String targetName = THREAD_LOCAL.get().name;
			log.warn("Target < {} > is already bound to current thread", targetName);
			throw new IllegalStateException("Target < " + targetName + " > is already bound to current thread");
		}
		THREAD_LOCAL.set(this);
	}

	public void release() {
		if (THREAD_LOCAL.get() != this) {
			log.warn("Target < {} > is not bound to current thread", name);
			throw new IllegalStateException("Target < " + name + " > is not bound to current thread");
		}
		THREAD_LOCAL.remove();
	}

	public boolean current() {
		return THREAD_LOCAL.get() == this;
	}
}
