package mylie.engine.core;

import java.util.Optional;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public final class Engine {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<Core> core = Optional.empty();
	private static final String NOT_RUNNING = "Engine is not running";
	private static final String ALREADY_RUNNING = "Engine is already running";
	private Engine() {
		throw new IllegalInstantiationException(Engine.class);
	}

	static void initialize(EngineSettings engineSettings) {
		if (core.isPresent()) {
			throw new IllegalStateException(ALREADY_RUNNING);
		}
		core = Optional.of(new Core(engineSettings));
		core.get().onInit();
	}

	static Core core() {
		if (core.isEmpty()) {
			throw new IllegalStateException(NOT_RUNNING);
		}
		return core.get();
	}

	static void clear() {
		core = Optional.empty();
	}

	public static void shutdown(ShutdownReason reason) {
		core().shutdownReason(reason);
	}

	public static void shutdown(Throwable reason) {
		Engine.shutdown(new ShutdownReason.Error(reason));
	}

	public static void shutdown(String reason) {
		Engine.shutdown(new ShutdownReason.Normal(reason));
	}

	public static void restart() {
		Engine.shutdown(new ShutdownReason.Restart(core().settings()));
	}

	static ShutdownReason shutdownReason() {
		return core().shutdownReason();
	}

	static void update() {

		core().onUpdate();
	}

	static void destroy() {
		core().onDestroy();
	}
}
