package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.core.async.Scheduler;

final class Core {
	@Getter
	private final ComponentManager componentManager;
	@Getter
	private final EngineSettings settings;
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private ShutdownReason shutdownReason;

	Core(EngineSettings settings) {
		this.settings = settings;
		componentManager = new ComponentManager();
		Vault vault = componentManager.addComponent(Vault.class);
		vault.addItem(settings);
		componentManager.addComponent(Scheduler.class);
		componentManager.addComponent(Timer.class);
	}

	void onInit() {

	}

	void onUpdate() {
		componentManager.component(Timer.class).onUpdate();
	}

	void onDestroy() {

	}

}
