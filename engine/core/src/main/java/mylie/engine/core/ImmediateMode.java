package mylie.engine.core;

import static mylie.engine.core.Engine.*;
import static mylie.engine.core.Engine.core;

import java.util.concurrent.*;
import mylie.engine.core.async.Scheduler;
import mylie.engine.util.LatchUtils;
import mylie.engine.util.QueueUtils;

public class ImmediateMode extends Application {
	private static boolean initialized = false;
	private static Thread updateLoopThread;
	private static final BlockingQueue<Runnable> updateQueue = new LinkedBlockingQueue<>();
	private static final BlockingQueue<Runnable> mainThreadQueue = new LinkedBlockingQueue<>();
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
		ShutdownReason shutdownReason = scheduler.multiThreaded()
				? updateMultiThreaded(scheduler)
				: updateSingleThreaded(scheduler);
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

	public static ShutdownReason updateMultiThreaded(Scheduler scheduler) {
		if (!initialized) {
			initialized = true;
			scheduler.register(Engine.TARGET, mainThreadQueue::add);
			updateLoopThread = new Thread(() -> {
				while (!Thread.interrupted()) {
					Runnable poll = QueueUtils.poll(updateQueue, 16, TimeUnit.MILLISECONDS);
					if (poll != null) {
						poll.run();
					}
				}
			}, "UpdateLoop");
			updateLoopThread.start();
		}
		CountDownLatch latch = new CountDownLatch(1);
		updateQueue.add(() -> {
			Engine.update();
			latch.countDown();
		});
		while (latch.getCount() > 0) {
			Runnable poll = QueueUtils.poll(mainThreadQueue, 16, TimeUnit.MILLISECONDS);
			if (poll != null) {
				poll.run();
			}
		}
		ShutdownReason shutdownReason = Engine.shutdownReason();
		if (shutdownReason != null) {
			CountDownLatch latch1 = new CountDownLatch(1);
			updateQueue.add(() -> {
				updateLoopThread.interrupt();
				latch1.countDown();
			});
			LatchUtils.await(latch1);
			scheduler.unregister(Engine.TARGET);
			initialized = false;
		}
		return shutdownReason;
	}

	public static ShutdownReason updateSingleThreaded(Scheduler scheduler) {
		if (!initialized) {
			initialized = true;
			scheduler.register(Engine.TARGET, mainThreadQueue::add);
		}
		Engine.update();
		ShutdownReason shutdownReason = shutdownReason();
		if (shutdownReason != null) {
			scheduler.unregister(Engine.TARGET);
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
}
