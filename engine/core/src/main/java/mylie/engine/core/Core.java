package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.async.Cache;
import mylie.engine.core.async.Scheduler;
@Slf4j
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
		Cache.registerDefaults(componentManager.component(Scheduler.class));
	}

	void onInit() {
		if (log.isTraceEnabled()) {
			log.trace("Initializing...");
		}
		componentManager.addComponent(ApplicationManager.class);
	}

	void onUpdate() {
		if (log.isTraceEnabled()) {
			log.trace("Updating...");
		}
		componentManager.component(Timer.class).onUpdate();
		componentManager.component(Scheduler.class).onUpdate();
		componentManager.updateComponents(true);
	}

	void onDestroy() {
		if (log.isTraceEnabled()) {
			log.trace("Shutting down...");
		}
		componentManager.component(Timer.class).onUpdate();
		componentManager.component(Scheduler.class).onUpdate();
		componentManager.updateComponents(false);
	}

}
