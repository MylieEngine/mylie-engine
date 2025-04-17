package mylie.engine.core;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.async.*;

public final class Components {
	private Components() {
	}

	public abstract static class App extends Base {
		protected App(ComponentManager manager) {
			super(manager, ExecutionMode.ASYNC, ApplicationManager.TARGET, Cache.ONE_FRAME);
		}

		@Override
		protected void onEnable() {
			super.onEnable();
			ApplicationManager applicationManager = Objects.requireNonNull(component(ApplicationManager.class));
			applicationManager.updateTask().dependencies().add(this.updateTask());
			updateTask().dependencies().add(applicationManager.applicationUpdateTask());
		}

		@Override
		protected void onDisable() {
			super.onDisable();
			ApplicationManager applicationManager = Objects.requireNonNull(component(ApplicationManager.class));
			applicationManager.updateTask().dependencies().remove(this.updateTask());
			updateTask().dependencies().remove(applicationManager.applicationUpdateTask());
		}
	}

	public abstract static class Core extends Base {
		protected Core(ComponentManager manager) {
			super(manager, ExecutionMode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME);
		}

		protected Core(ComponentManager manager, ExecutionMode executionMode, Target target, Cache cache) {
			super(manager, executionMode, target, cache);
		}
	}

	@Slf4j
	@Getter
	abstract static class Base extends Component {
		private final Task<Boolean> updateTask;
		@Setter(AccessLevel.PACKAGE)
		private boolean initialized;
		@Setter(AccessLevel.PUBLIC)
		@Getter(AccessLevel.PUBLIC)
		private boolean enabled = true;
		@Setter(AccessLevel.PACKAGE)
		private boolean currentlyEnabled;
		Base(ComponentManager manager, ExecutionMode executionMode, Target target, Cache cache) {
			super(manager);
			updateTask = new UpdateTask(component(Scheduler.class), executionMode, target, cache,
					component(Timer.class), this);
		}

		protected void onInitialize() {
			if (log.isTraceEnabled()) {
				log.trace("Component< {} > initialized", this.getClass().getSimpleName());
			}
		}

		protected void onEnable() {
			if (log.isTraceEnabled()) {
				log.trace("Component< {} > enabled", this.getClass().getSimpleName());
			}
		}

		protected void onUpdate() {
			if (log.isTraceEnabled()) {
				log.trace("Component< {} > updated", this.getClass().getSimpleName());
			}

		}

		protected void onDisable() {
			if (log.isTraceEnabled()) {
				log.trace("Component< {} > disabled", this.getClass().getSimpleName());
			}
		}

		protected void onDestroy() {
			if (log.isTraceEnabled()) {
				log.trace("Component< {} > destroyed", this.getClass().getSimpleName());
			}
		}

		@AllArgsConstructor
		private static class UpdateTask extends Task<Boolean> {
			private final Scheduler scheduler;
			private final ExecutionMode executionMode;
			private final Target target;
			private final Cache cache;
			private final Timer timer;
			private final Base base;
			@Override
			protected Result<Boolean> executeTask() {
				Functions.One<Base, Boolean> toCall;
				if (base.manager().running()) {
					toCall = UPDATE_COMPONENT;
				} else {
					toCall = SHUTDOWN_COMPONENT;
				}
				return Async.async(scheduler, executionMode, target, cache, timer.currentTime().frameId(), toCall,
						base);
			}

			private static final Functions.One<Base, Boolean> UPDATE_COMPONENT = new Functions.One<>(
					"UPDATE_COMPONENT") {
				@Override
				protected Boolean execute(Base base) {
					if (!base.initialized()) {
						base.initialized(true);
						base.onInitialize();
					}
					if (base.enabled() != base.currentlyEnabled()) {
						if (base.enabled()) {
							base.currentlyEnabled(true);
							base.onEnable();
						} else {
							base.currentlyEnabled(false);
							base.onDisable();
						}
					}
					if (base.enabled()) {
						base.onUpdate();
					}
					return true;
				}
			};

			private static final Functions.One<Base, Boolean> SHUTDOWN_COMPONENT = new Functions.One<>(
					"UPDATE_COMPONENT") {
				@Override
				protected Boolean execute(Base base) {
					if (base.currentlyEnabled()) {
						base.currentlyEnabled(false);
						base.enabled(false);
						base.onDisable();
					}
					if (base.initialized()) {
						base.initialized(false);
						base.onDestroy();
					}
					return true;
				}
			};
		}
	}
}
