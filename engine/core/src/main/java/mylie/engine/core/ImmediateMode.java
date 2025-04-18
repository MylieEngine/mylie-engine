package mylie.engine.core;

import static mylie.engine.core.Engine.*;
import static mylie.engine.core.Engine.core;

import java.util.concurrent.*;
import mylie.engine.core.async.Scheduler;
import mylie.engine.util.CheckedExceptions;

public class ImmediateMode extends Application {
	private static boolean initialized = false;
	private static UpdateThread updateLoopThread;

	public ImmediateMode(ComponentManager manager) {
		super(manager);
	}

	@Override
	protected void onInitialize() {

	}

	@Override
	protected void onUpdate(Time time) {

	}

	@Override
	protected void onShutdown(ShutdownReason reason) {

	}

	public static <T extends Component> T addEngineComponent(Class<T> componentClass) {
		core().componentManager().addComponent(componentClass);
		return getEngineComponent(componentClass);
	}

	public static <T extends Component> T getEngineComponent(Class<T> componentClass) {
		return core().componentManager().component(componentClass);
	}

	public static void removeEngineComponent(Component component) {
		core().componentManager().removeComponent(component);
	}

	public static ShutdownReason start(EngineSettings engineSettings) {
		engineSettings.applicationClass(ImmediateMode.class);
		initialize(engineSettings);
		return core().shutdownReason();
	}

	public static ShutdownReason update() {
		Scheduler scheduler = core().componentManager().component(Scheduler.class);
		ShutdownReason shutdownReason = scheduler.multiThreaded() ? updateMultiThreaded() : updateSingleThreaded();
		if (shutdownReason != null) {
			if (shutdownReason instanceof ShutdownReason.Restart(EngineSettings engineSettings)) {
				if (core().settings().handleRestarts()) {
					destroy();
					clear();
					return start(engineSettings);
				}
			}
			destroy();
			clear();
			return shutdownReason;
		}
		return null;
	}

	public static ShutdownReason updateMultiThreaded() {
		if (!initialized) {
			initialized = true;
			updateLoopThread = new UpdateThread();
		}
		CheckedExceptions.await(updateLoopThread.entryBarrier);
		while (updateLoopThread.exitBarrier.getNumberWaiting() == 0) {
			Runnable poll = CheckedExceptions.poll(core().mainThreadQueue(), 16, TimeUnit.MILLISECONDS);
			if (poll != null) {
				poll.run();
			}
		}
		ShutdownReason shutdownReason = Engine.shutdownReason();
		if (shutdownReason != null) {
			updateLoopThread.running = false;
			CheckedExceptions.await(updateLoopThread.exitBarrier);
			CheckedExceptions.await(updateLoopThread.latch);
			initialized = false;
		} else {
			CheckedExceptions.await(updateLoopThread.exitBarrier);
		}
		return shutdownReason;
	}

	public static ShutdownReason updateSingleThreaded() {
		if (!initialized) {
			initialized = true;
		}
		Engine.update();
		ShutdownReason shutdownReason = shutdownReason();
		if (shutdownReason != null) {
			initialized = false;
		}
		return shutdownReason;
	}

	public static void shutdown(String reason) {
		Engine.shutdown(reason);
		ImmediateMode.update();
	}

	public static void shutdown(Throwable reason) {
		Engine.shutdown(reason);
		ImmediateMode.update();
	}

	public static void restart() {
		Engine.restart();
	}

	private static class UpdateThread implements Runnable {
		private final CyclicBarrier entryBarrier;
		private final CyclicBarrier exitBarrier;
		private final CountDownLatch latch;

		public UpdateThread() {
			this.entryBarrier = new CyclicBarrier(2);
			this.exitBarrier = new CyclicBarrier(2);
			this.latch = new CountDownLatch(1);
			Thread thread = new Thread(this, "UpdateLoop");
			thread.start();
		}
		private boolean running;
		@Override
		public void run() {
			running = true;
			while (running) {
				CheckedExceptions.await(entryBarrier);
				Engine.update();
				CheckedExceptions.await(exitBarrier);
			}
			latch.countDown();
		}
	}

}
