package mylie.engine.core.async;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Component;
import mylie.engine.core.EngineSettings;
import mylie.engine.core.Vault;

@Slf4j
public final class Scheduler extends Component {
	private SchedulingStrategy schedulingStrategy;
	private final Map<Target, SchedulingStrategy.TaskExecutor> taskExecutors = new ConcurrentHashMap<>();
	private final List<Cache> caches = new ArrayList<>();
	Scheduler(SchedulingStrategy strategy) {
		super();
		this.schedulingStrategy = strategy;
		onInitialize();
	}
	public Scheduler() {

	}

	private void onInitialize() {
		loadSettings();
		taskExecutors.put(Target.BACKGROUND, schedulingStrategy.executor(Target.BACKGROUND, null));
	}

	@Override
	protected void onAdded() {
		super.onAdded();
		onInitialize();
	}

	public WorkerThread createWorkerThread(Target target) {
		WorkerThread workerThread = schedulingStrategy.createWorkerThread(target);
		register(target, workerThread.drain());
		return workerThread;
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
	}

	private void createDefaultStrategy() {
		schedulingStrategy = new SchedulingStrategies.MultiThreadExecutor(ForkJoinPool.commonPool());
	}

	public void onUpdate() {
		for (Cache cache : caches) {
			cache.onUpdate();
		}
	}

	public void submit(Runnable runnable, Target target) {
		SchedulingStrategy.TaskExecutor taskExecutor = taskExecutors.get(target);
		if (taskExecutor == null) {
			throw new IllegalArgumentException("Target< " + target.name() + " > not registered");
		} else {
			if (taskExecutor instanceof SchedulingStrategies.SubmitExecutor submitExecutor) {
				submitExecutor.drain().accept(runnable);
			} else if (taskExecutor instanceof SchedulingStrategies.DirectExecutor) {
				runnable.run();
			} else {
				taskExecutor.execute(Result.of(target, null, 0, () -> {
					runnable.run();
					return true;
				}));
			}
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
		if (log.isTraceEnabled()) {
			log.trace("Target< {} > unregistered", target.name());
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
