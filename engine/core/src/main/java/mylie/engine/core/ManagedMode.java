package mylie.engine.core;

import static mylie.engine.core.Engine.core;

import java.util.concurrent.TimeUnit;
import mylie.engine.core.async.Scheduler;
import mylie.engine.util.CheckedExceptions;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public class ManagedMode {
	private ManagedMode() {
		throw new IllegalInstantiationException(ManagedMode.class);
	}

	public static ShutdownReason start(EngineSettings engineSettings, Class<? extends Application> applicationClass) {
		engineSettings.applicationClass(applicationClass);
		Engine.initialize(engineSettings);
		Scheduler scheduler = core().componentManager().component(Scheduler.class);
		ShutdownReason shutdownReason = null;
		while (shutdownReason == null) {
			shutdownReason = scheduler.multiThreaded() ? runMultiThreaded() : runSingleThreaded();
			if (shutdownReason instanceof ShutdownReason.Restart(EngineSettings settings)) {
				if (engineSettings.handleRestarts()) {
					Engine.clear();
					Engine.initialize(settings);
					scheduler = core().componentManager().component(Scheduler.class);
					shutdownReason = null;
				}
			}
		}
		Engine.clear();
		return shutdownReason;
	}

	private static ShutdownReason runMultiThreaded() {
		Thread updateLoop = new Thread(ManagedMode::updateLoopThread, "UpdateLoop");
		updateLoop.start();
		while (Engine.shutdownReason() == null || updateLoop.isAlive()) {
			Runnable command = CheckedExceptions.poll(core().mainThreadQueue(), 16, TimeUnit.MILLISECONDS);
			if (command != null) {
				command.run();
			}
		}
		while (!core().mainThreadQueue().isEmpty()) {
			Runnable poll = CheckedExceptions.poll(core().mainThreadQueue(), 16, TimeUnit.MILLISECONDS);
			if (poll != null) {
				poll.run();
			}
		}
		return Engine.shutdownReason();
	}

	private static void updateLoopThread() {
		while (Engine.shutdownReason() == null) {
			Engine.update();
		}
		Engine.destroy();
	}

	private static ShutdownReason runSingleThreaded() {
		ShutdownReason shutdownReason = null;
		while (shutdownReason == null) {
			Engine.update();
			shutdownReason = Engine.shutdownReason();
		}
		Engine.destroy();
		return shutdownReason;
	}
}
