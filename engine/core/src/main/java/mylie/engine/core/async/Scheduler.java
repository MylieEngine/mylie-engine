package mylie.engine.core.async;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Component;
import mylie.engine.core.ComponentManager;
@Slf4j
public final class Scheduler extends Component {
	private SchedulingStrategy strategy;
	private final Map<Target, SchedulingStrategy.TaskExecutor> taskExecutors = new ConcurrentHashMap<>();
	private final List<Cache> caches = new ArrayList<>();
	Scheduler(SchedulingStrategy strategy) {
		super(null);
		this.strategy = strategy;
		onInitialize();
	}
	public Scheduler(ComponentManager manager) {
		super(manager);
		onInitialize();
	}

	private void onInitialize() {
		taskExecutors.put(Target.BACKGROUND, strategy.executor(Target.BACKGROUND, null));
	}

	public void onUpdate() {
		for (Cache cache : caches) {
			cache.onUpdate();
		}
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
		SchedulingStrategy.TaskExecutor taskExecutor = strategy.executor(target, drain);
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

	interface SchedulingStrategy {
		TaskExecutor executor(Target target, Consumer<Runnable> drain);

		boolean multiThread();

		interface TaskExecutor {
			<R> void execute(Result<R> result);
		}

		class SubmitExecutor implements TaskExecutor {
			@Getter
			private final Target target;
			private final Consumer<Runnable> drain;

			public SubmitExecutor(Target target, Consumer<Runnable> drain) {
				this.target = target;
				this.drain = drain;
			}

			@Override
			public <R> void execute(Result<R> result) {
				drain.accept(() -> Async.executeTask(result));
			}
		}

		class DirectExecutor implements TaskExecutor {
			@Override
			public <R> void execute(Result<R> result) {
				Async.executeTask(result);
			}
		}
	}
}
