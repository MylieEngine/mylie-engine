package mylie.engine.core;

import mylie.engine.util.exceptions.IllegalInstantiationException;

public final class Engine {
	private static Core core;
	private static final String NOT_RUNNING = "Engine is not running";
	private static final String ALREADY_RUNNING = "Engine is already running";
	private Engine() {
		throw new IllegalInstantiationException(Engine.class);
	}

	static void initialize(EngineSettings engineSettings) {
		if (core != null) {
			throw new IllegalStateException(ALREADY_RUNNING);
		}
		core = new Core(engineSettings);
		core.onInit();
	}

	static Core core() {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		return core;
	}

	static void clear() {
		core = null;
	}

	public static void shutdown(ShutdownReason reason) {
		core.shutdownReason(reason);
	}

	public static void shutdown(Throwable reason) {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		Engine.shutdown(new ShutdownReason.Error(reason));
	}

	public static void shutdown(String reason) {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		Engine.shutdown(new ShutdownReason.Normal(reason));
	}

	public static void restart() {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		Engine.shutdown(new ShutdownReason.Restart(core.settings()));
	}

	static ShutdownReason shutdownReason() {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		return core.shutdownReason();
	}

	static void update() {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		core.onUpdate();
	}

	static void destroy() {
		core.onDestroy();
	}
}
