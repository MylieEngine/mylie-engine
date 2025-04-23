package mylie.engine.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.async.Cache;
import mylie.engine.core.async.Scheduler;
import mylie.engine.event.EventManager;
import mylie.engine.input.InputManager;

@Slf4j
final class Core {
	@Getter
	private final ComponentManager componentManager;
	@Getter
	private final EngineSettings settings;
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private ShutdownReason shutdownReason;
	@Getter(AccessLevel.PACKAGE)
	private final BlockingQueue<Runnable> mainThreadQueue = new LinkedBlockingQueue<>();
	Core(EngineSettings settings) {
		this.settings = settings;
		componentManager = new ComponentManager();
		Vault vault = componentManager.addComponent(new Vault());
		vault.addItem(settings);
		componentManager.addComponent(new Scheduler());
		componentManager.addComponent(new Timer());
		initScheduler();
		componentManager.addComponent(new EventManager());
		componentManager.addComponent(new InputManager());
	}

	private void initScheduler() {
		Cache.registerDefaults(componentManager.component(Scheduler.class));
		componentManager().component(Scheduler.class).register(Engine.TARGET, mainThreadQueue::add);
	}

	void onInit() {
		if (log.isTraceEnabled()) {
			log.trace("Initializing...");
		}
		componentManager.addComponent(new ApplicationManager());
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
		Scheduler scheduler = componentManager.component(Scheduler.class);
		componentManager.updateComponents(false);
		scheduler.unregister(Engine.TARGET);
	}

}
