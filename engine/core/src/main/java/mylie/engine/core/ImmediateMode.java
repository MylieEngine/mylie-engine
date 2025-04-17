package mylie.engine.core;

import static mylie.engine.core.Engine.*;
import static mylie.engine.core.Engine.core;

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
		core().componentManager().addComponent(componentClass);
		return getEngineComponent(componentClass);
	}

	public static <T extends Component> T getEngineComponent(Class<T> componentClass) {
		return core().componentManager().component(componentClass);
	}

	public static void removeEngineComponent(Component component) {
		core().componentManager().removeComponent(component);
	}

	public static ShutdownReason start(EngineSettings engineSettings) {
		engineSettings.applicationClass(ImmediateMode.class);
		initialize(engineSettings);
		return core().shutdownReason();
	}

	public static ShutdownReason update() {
		Engine.update();
		if (core().shutdownReason() != null) {
			ShutdownReason reason = core().shutdownReason();
			if (reason instanceof ShutdownReason.Restart(EngineSettings engineSettings)) {
				if (core().settings().handleRestarts()) {
					destroy();
					clear();
					return start(engineSettings);
				}
			}
			destroy();
			clear();
			return reason;
		}
		return null;
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
