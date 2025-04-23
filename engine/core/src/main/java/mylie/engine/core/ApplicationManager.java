package mylie.engine.core;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.core.async.*;

public class ApplicationManager extends Components.Core {
	static final Target TARGET = new Target("Application-Thread");
	private final Queue<Runnable> applicationQueue;
	private WorkerThread workerThread;
	@Getter(AccessLevel.PACKAGE)
	private Task<Boolean> applicationUpdateTask;
	private Application application;
	private Timer timer;
	public ApplicationManager() {
		super(ExecutionMode.ASYNC, TARGET, Cache.ONE_FRAME);

		applicationQueue = new ConcurrentLinkedQueue<>();

	}

	@Override
	protected void onAdded() {
		super.onAdded();
		Vault vault = Objects.requireNonNull(component(Vault.class));
		EngineSettings settings = Objects.requireNonNull(vault.item(EngineSettings.class));
		addComponent(settings.application());
		application = Objects.requireNonNull(component(Application.class));
		timer = Objects.requireNonNull(component(Timer.class));
		Scheduler scheduler = Objects.requireNonNull(component(Scheduler.class));
		scheduler.register(Application.TARGET, applicationQueue::add);
		workerThread = scheduler.createWorkerThread(TARGET);
		applicationUpdateTask = new ApplicationUpdateTask(this);
		updateTask().dependencies().add(applicationUpdateTask);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		application.onUpdate(timer.currentTime());
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		application.onInitialize();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.onShutdown(Engine.shutdownReason());
	}

	@Override
	protected void onRemoved() {
		super.onRemoved();
		updateTask().dependencies().remove(applicationUpdateTask);
		workerThread.stop();
	}

	private static class ApplicationUpdateTask extends Task<Boolean> {
		private final ApplicationManager applicationManager;
		private final Scheduler scheduler;
		private final Timer timer;
		private ApplicationUpdateTask(ApplicationManager applicationManager) {
			this.applicationManager = applicationManager;
			this.scheduler = Objects.requireNonNull(applicationManager.component(Scheduler.class));
			this.timer = Objects.requireNonNull(applicationManager.component(Timer.class));
		}

		@Override
		protected Result<Boolean> executeTask() {
			return Async.async(scheduler, ExecutionMode.ASYNC, TARGET, Cache.ONE_FRAME, timer.currentTime().frameId(),
					APPLICATION_UPDATE, applicationManager);
		}

		private static final Functions.One<ApplicationManager, Boolean> APPLICATION_UPDATE = new Functions.One<>(
				"Application-Update") {
			@Override
			protected Boolean execute(ApplicationManager appManager) {
				while (!appManager.applicationQueue.isEmpty()) {
					appManager.applicationQueue.poll().run();
				}
				return true;
			}
		};
	}
}
