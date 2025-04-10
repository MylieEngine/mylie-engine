package mylie.engine.core;

import mylie.engine.util.ClassUtils;

public abstract class Platform {

	protected Platform() {

	}

	protected abstract void onInitialize(EngineSettings engineSettings);

	public static EngineSettings initialize(Class<? extends Platform> platformClass) {
		Platform platform = ClassUtils.newInstance(platformClass);
		EngineSettings engineSettings = new EngineSettings();
		platform.onInitialize(engineSettings);
		return engineSettings;
	}

}
