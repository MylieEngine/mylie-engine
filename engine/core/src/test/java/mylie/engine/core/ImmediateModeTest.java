package mylie.engine.core;

import static mylie.engine.core.async.AsyncTestData.SCHEDULING_STRATEGIES_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import mylie.engine.core.async.Scheduler;
import mylie.engine.core.async.SchedulingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ImmediateModeTest {

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycle(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		ShutdownReason reason;
		reason = ImmediateMode.start(engineSettings);
		assertNull(reason);
		reason = ImmediateMode.update();
		assertNull(reason);
		ImmediateMode.shutdown("OK");
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycleWithException(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		ShutdownReason reason;
		reason = ImmediateMode.start(engineSettings);
		assertNull(reason);
		reason = ImmediateMode.update();
		assertNull(reason);
		ImmediateMode.shutdown(new RuntimeException());
	}

	@Test
	public void testNotInitialized() {
		assertThrows(IllegalStateException.class, ImmediateMode::update);
		assertThrows(IllegalStateException.class, () -> ImmediateMode.shutdown("OK"));
		assertThrows(IllegalStateException.class, ImmediateMode::restart);
		assertThrows(IllegalStateException.class, () -> ImmediateMode.shutdown(new RuntimeException()));

	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testDoubleStart(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		assertDoesNotThrow(() -> ImmediateMode.start(engineSettings));
		assertThrows(IllegalStateException.class, () -> ImmediateMode.start(engineSettings));
		ImmediateMode.shutdown("OK");
		// ImmediateMode.update();
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testHandleRestarts(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		assertNull(ImmediateMode.start(engineSettings));
		ImmediateMode.restart();
		assertNull(ImmediateMode.update());
		ImmediateMode.shutdown("OK");
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testHandleRestartsFalse(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(false);
		assertNull(ImmediateMode.start(engineSettings));
		ImmediateMode.restart();
		ShutdownReason reason = ImmediateMode.update();
		assertInstanceOf(ShutdownReason.Restart.class, reason);
		ImmediateMode.start(((ShutdownReason.Restart) (reason)).engineSettings());
		ImmediateMode.shutdown("OK");
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycleCorrectness(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		ImmediateMode.start(engineSettings);
		ObservableComponent component = ImmediateMode.addEngineComponent(ObservableComponent.class);
		CoreTestComponent coreTestComponent = ImmediateMode.addEngineComponent(CoreTestComponent.class);
		assertEquals(1, component.observeAdded);
		assertEquals(0, component.observeEnabled);
		assertEquals(0, component.observeInitialize);
		ImmediateMode.update();
		assertEquals(1, component.observeInitialize);
		assertEquals(1, component.observeEnabled);
		assertEquals(1, component.observeUpdate);
		ImmediateMode.update();
		assertEquals(1, component.observeInitialize);
		assertEquals(1, component.observeEnabled);
		assertEquals(2, component.observeUpdate);
		component.enabled(false);
		ImmediateMode.update();
		assertEquals(1, component.observeEnabled);
		assertEquals(1, component.observeDisable);
		assertEquals(2, component.observeUpdate);
		ImmediateMode.update();
		assertEquals(1, component.observeEnabled);
		assertEquals(1, component.observeDisable);
		assertEquals(2, component.observeUpdate);
		component.enabled(true);
		ImmediateMode.update();
		assertEquals(2, component.observeEnabled);
		assertEquals(1, component.observeDisable);
		assertEquals(3, component.observeUpdate);
		ImmediateMode.removeEngineComponent(component);
		boolean multiThreaded = ImmediateMode.getEngineComponent(Scheduler.class).multiThreaded();
		ImmediateMode.getEngineComponent(Scheduler.class).submit(() -> {
			if (multiThreaded) {
				Assertions.assertEquals("Application-Thread", Thread.currentThread().getName());
			}
		}, Application.TARGET);
		ImmediateMode.update();
		assertEquals(2, component.observeEnabled);
		assertEquals(2, component.observeDisable);
		assertEquals(3, component.observeUpdate);
		assertEquals(1, component.observeRemoved);
		assertEquals(1, component.observeDestroy);
		ImmediateMode.shutdown("OK");
		assertTrue(coreTestComponent.initialized);
		assertEquals(component.observeUpdate, component.observeCount);
	}

	public static class CoreTestComponent extends Components.Core {
		boolean initialized;
		public CoreTestComponent(ComponentManager manager) {
			super(manager);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			initialized = true;
		}
	}

	public static class ObservableComponent extends Components.App {
		private int observeEnabled;
		private int observeDisable;
		private int observeAdded;
		private int observeRemoved;
		private int observeUpdate;
		private int observeInitialize;
		private int observeDestroy;
		private int observeCount;
		public ObservableComponent(ComponentManager manager) {
			super(manager);
		}

		@Override
		protected void onEnable() {
			super.onEnable();
			observeEnabled++;
		}

		@Override
		protected void onDisable() {
			super.onDisable();
			observeDisable++;
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			observeInitialize++;
		}

		@Override
		protected void onUpdate() {
			super.onUpdate();
			observeUpdate++;
			Objects.requireNonNull(component(Scheduler.class)).submit(() -> observeCount++, Engine.TARGET);
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			observeDestroy++;
		}

		@Override
		protected void onAdded() {
			super.onAdded();
			observeAdded++;
		}

		@Override
		protected void onRemoved() {
			super.onRemoved();
			observeRemoved++;
		}
	}
}
