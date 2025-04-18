package mylie.engine.core;

import mylie.engine.core.async.Target;

public abstract class Application extends Component {
	public static final Target TARGET = new Target("Application", false, false);

	protected Application(ComponentManager manager) {
		super(manager);
	}

	protected abstract void onInitialize();

	protected abstract void onUpdate(Time time);

	protected abstract void onShutdown(ShutdownReason reason);
}
