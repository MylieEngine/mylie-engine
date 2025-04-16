package mylie.engine.core.async;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Component;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.EngineSettings;
import mylie.engine.core.Vault;

@Slf4j
public final class Scheduler extends Component {
	private SchedulingStrategy schedulingStrategy;
	private final Map<Target, SchedulingStrategy.TaskExecutor> taskExecutors = new ConcurrentHashMap<>();
	private final List<Cache> caches = new ArrayList<>();
	Scheduler(SchedulingStrategy strategy) {
		super(null);
		this.schedulingStrategy = strategy;
		onInitialize();
	}
	public Scheduler(ComponentManager manager) {
		super(manager);
		onInitialize();
	}

	private void onInitialize() {
		loadSettings();
		taskExecutors.put(Target.BACKGROUND, schedulingStrategy.executor(Target.BACKGROUND, null));
	}

	private void loadSettings() {
		if (schedulingStrategy == null) {
			Vault vault = component(Vault.class);
			if (vault == null) {
				createDefaultStrategy();
				return;
			}
			EngineSettings settings = vault.item(EngineSettings.class);
			if (settings == null) {
				createDefaultStrategy();
				return;
			}
			schedulingStrategy = settings.schedulingStrategy();
		}
		if (schedulingStrategy == null) {
			createDefaultStrategy();
		}
	}

	private void createDefaultStrategy() {
		schedulingStrategy = new SchedulingStrategies.MultiThreadExecutor(ForkJoinPool.commonPool());
	}

	public void onUpdate() {
		for (Cache cache : caches) {
			cache.onUpdate();
		}
	}

	public void strategy(SchedulingStrategy strategy) {
		if (strategy.multiThread() != multiThreaded()) {
			throw new IllegalArgumentException(
					"Cannot change scheduling strategy to " + strategy + " when multiThreaded is " + multiThreaded());
		}
		this.schedulingStrategy = strategy;
	}

	public boolean multiThreaded() {
		return schedulingStrategy.multiThread();
	}

	public <R> void executeTask(Target target, Result<R> result) {
		SchedulingStrategy.TaskExecutor taskExecutor = taskExecutors.get(target);
		if (taskExecutor == null) {
			log.warn("Target< {} > not registered", target.name());
			throw new IllegalArgumentException("Target< " + target.name() + " > not registered");
		}
		taskExecutor.execute(result);
	}

	public void register(Target target, Consumer<Runnable> drain) {
		if (taskExecutors.containsKey(target)) {
			log.warn("Target< {} > already registered", target.name());
			throw new IllegalArgumentException("Target< " + target.name() + " > already registered");
		}
		SchedulingStrategy.TaskExecutor taskExecutor = schedulingStrategy.executor(target, drain);
		taskExecutors.put(target, taskExecutor);
		if (log.isTraceEnabled()) {
			log.trace("Target< {} > registered", target.name());
		}
	}

	public void unregister(Target target) {
		if (!taskExecutors.containsKey(target)) {
			log.warn("Target< {} > not registered", target.name());
			throw new IllegalArgumentException("Target< " + target.name() + " > not registered");
		}
		taskExecutors.remove(target);
	}

	public void register(Cache cache) {
		if (caches.contains(cache)) {
			throw new IllegalArgumentException("Cache< " + cache.getClass().getSimpleName() + " > already registered");
		}
		caches.add(cache);
		cache.clear();
		if (log.isTraceEnabled()) {
			log.trace("Cache< {} > registered", cache.getClass().getSimpleName());
		}
	}

	public void unregister(Cache cache) {
		if (!caches.remove(cache)) {
			throw new IllegalArgumentException("Cache< " + cache.getClass().getSimpleName() + " > not registered");
		}
		caches.remove(cache);
		if (log.isTraceEnabled()) {
			log.trace("Cache< {} > unregistered", cache.getClass().getSimpleName());
		}
	}
}
