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
		core = new Core(engineSettings);
		core.onInit();
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
		core.onUpdate();
	}

	static void destroy() {
		core.onDestroy();
	}

	public static final class ImmediateMode {
		private ImmediateMode() {
			throw new IllegalInstantiationException(ImmediateMode.class);
		}

		public static ShutdownReason start(EngineSettings engineSettings) {
			if (core != null) {
				throw new IllegalStateException(ALREADY_RUNNING);
			}
			engineSettings.applicationClass(mylie.engine.core.ImmediateMode.class);
			Engine.initialize(engineSettings);
			return core.shutdownReason();
		}

		public static ShutdownReason update() {
			if (core == null) {
				throw new IllegalStateException(NOT_RUNNING);
			}
			Engine.update();
			if (core.shutdownReason() != null) {
				ShutdownReason reason = core.shutdownReason();
				if (reason instanceof ShutdownReason.Restart(EngineSettings engineSettings)) {
					if (core.settings().handleRestarts()) {
						Engine.destroy();
						core = null;
						return start(engineSettings);
					}
				}
				Engine.destroy();
				core = null;
				return reason;
			}
			return null;
		}

		public static void shutdown(String reason) {
			Engine.shutdown(reason);
		}

		public static void shutdown(Throwable reason) {
			Engine.shutdown(reason);
		}

		public static void restart() {
			Engine.restart();
		}
	}
}
