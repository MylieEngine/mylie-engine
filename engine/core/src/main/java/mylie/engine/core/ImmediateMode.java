package mylie.engine.core;

import static mylie.engine.core.Engine.*;

public class ImmediateMode extends Application {
	public ImmediateMode(ComponentManager manager) {
		super(manager);
	}

	@Override
	protected void onInitialize() {

	}

	@Override
	protected void onUpdate(Time time) {

	}

	@Override
	protected void onShutdown(ShutdownReason reason) {

	}

	public static <T extends Component> T addEngineComponent(Class<T> componentClass) {
		checkCoreRunning();
		core.componentManager().addComponent(componentClass);
		return getEngineComponent(componentClass);
	}

	public static <T extends Component> T getEngineComponent(Class<T> componentClass) {
		checkCoreRunning();
		return core.componentManager().component(componentClass);
	}

	public static void removeEngineComponent(Component component) {
		core.componentManager().removeComponent(component);
	}

	public static ShutdownReason start(EngineSettings engineSettings) {
		if (core != null) {
			throw new IllegalStateException(ALREADY_RUNNING);
		}
		engineSettings.applicationClass(mylie.engine.core.ImmediateMode.class);
		initialize(engineSettings);
		return core.shutdownReason();
	}

	public static ShutdownReason update() {
		checkCoreRunning();
		Engine.update();
		if (core.shutdownReason() != null) {
			ShutdownReason reason = core.shutdownReason();
			if (reason instanceof ShutdownReason.Restart(EngineSettings engineSettings)) {
				if (core.settings().handleRestarts()) {
					destroy();
					core = null;
					return start(engineSettings);
				}
			}
			destroy();
			core = null;
			return reason;
		}
		return null;
	}

	private static void checkCoreRunning() {
		if (core == null) {
			throw new IllegalStateException(NOT_RUNNING);
		}
	}

	public static void shutdown(String reason) {
		Engine.shutdown(reason);
		ImmediateMode.update();
	}

	public static void shutdown(Throwable reason) {
		Engine.shutdown(reason);
		ImmediateMode.update();
	}

	public static void restart() {
		Engine.restart();
	}
}
