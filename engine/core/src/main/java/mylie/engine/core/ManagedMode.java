package mylie.engine.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import mylie.engine.core.async.Scheduler;
import mylie.engine.util.QueueUtils;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public class ManagedMode {
	private static BlockingQueue<Runnable> mainThreadQueue;
	private ManagedMode() {
		throw new IllegalInstantiationException(ManagedMode.class);
	}

	public static ShutdownReason start(EngineSettings engineSettings, Class<? extends Application> applicationClass) {
		mainThreadQueue = new LinkedBlockingQueue<>();
		engineSettings.applicationClass(applicationClass);
		Engine.initialize(engineSettings);
		Scheduler scheduler = Engine.core().componentManager().component(Scheduler.class);
		ShutdownReason shutdownReason = null;
		while (shutdownReason == null) {
			shutdownReason = scheduler.multiThreaded() ? runMultiThreaded(scheduler) : runSingleThreaded(scheduler);
			if (shutdownReason instanceof ShutdownReason.Restart(EngineSettings settings)) {
				if (engineSettings.handleRestarts()) {
					Engine.clear();
					Engine.initialize(settings);
					scheduler = Engine.core().componentManager().component(Scheduler.class);
					shutdownReason = null;
				}
			}
		}
		Engine.clear();
		return shutdownReason;
	}

	private static ShutdownReason runMultiThreaded(Scheduler scheduler) {
		scheduler.register(Engine.TARGET, mainThreadQueue::add);
		Thread updateLoop = new Thread(ManagedMode::updateLoopThread, "UpdateLoop");
		updateLoop.start();
		while (Engine.shutdownReason() == null || updateLoop.isAlive()) {
			Runnable command = QueueUtils.poll(mainThreadQueue, 16, TimeUnit.MILLISECONDS);
			if (command != null) {
				command.run();
			}
		}
		scheduler.unregister(Engine.TARGET);
		return Engine.shutdownReason();
	}

	private static void updateLoopThread() {
		while (Engine.shutdownReason() == null) {
			Engine.update();
		}
		Engine.destroy();
	}

	private static ShutdownReason runSingleThreaded(Scheduler scheduler) {
		scheduler.register(Engine.TARGET, mainThreadQueue::add);
		ShutdownReason shutdownReason = null;
		while (shutdownReason == null) {
			Engine.update();
			shutdownReason = Engine.shutdownReason();
		}
		Engine.destroy();
		scheduler.unregister(Engine.TARGET);
		return shutdownReason;
	}
}
