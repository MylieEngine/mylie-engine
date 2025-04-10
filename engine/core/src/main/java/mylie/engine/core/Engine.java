package mylie.engine.core;

import mylie.engine.util.exceptions.IllegalInstantiationException;

public final class Engine {
	private static Core core;
	private Engine() {
		throw new IllegalInstantiationException(Engine.class);
	}

	protected static void initialize(EngineSettings engineSettings) {
		core = new Core(engineSettings);
		core.onInit();
	}

	protected static void update() {
		core.onUpdate();
	}

	protected static void destroy() {
		core.onDestroy();
	}

}
