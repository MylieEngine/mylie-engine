package mylie.engine.core;

import mylie.engine.TestUtils;
import mylie.engine.core.async.SchedulingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static mylie.engine.core.async.AsyncTestData.SCHEDULER_SOURCE;
import static mylie.engine.core.async.AsyncTestData.SCHEDULING_STRATEGIES_SOURCE;

public class ImmediateModeTest {

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycle(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		ShutdownReason reason;
		reason = Engine.ImmediateMode.start(engineSettings);
		Assertions.assertNull(reason);
		reason = Engine.ImmediateMode.update();
		Assertions.assertNull(reason);
		Engine.ImmediateMode.shutdown("OK");
		reason = Engine.ImmediateMode.update();
		Assertions.assertNotNull(reason);
		Assertions.assertInstanceOf(ShutdownReason.Normal.class, reason);
		Assertions.assertEquals("OK", ((ShutdownReason.Normal) reason).reason());
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycleWithException(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		ShutdownReason reason;
		reason = Engine.ImmediateMode.start(engineSettings);
		Assertions.assertNull(reason);
		reason = Engine.ImmediateMode.update();
		Assertions.assertNull(reason);
		Engine.ImmediateMode.shutdown(new RuntimeException());
		reason = Engine.ImmediateMode.update();
		Assertions.assertNotNull(reason);
		Assertions.assertInstanceOf(ShutdownReason.Error.class, reason);
	}

	@Test
	public void testNotInitialized() {
		Assertions.assertThrows(IllegalStateException.class, Engine.ImmediateMode::update);
		Assertions.assertThrows(IllegalStateException.class, () -> Engine.ImmediateMode.shutdown("OK"));
		Assertions.assertThrows(IllegalStateException.class, Engine.ImmediateMode::restart);
		Assertions.assertThrows(IllegalStateException.class,
				() -> Engine.ImmediateMode.shutdown(new RuntimeException()));

	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testDoubleStart(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		Assertions.assertDoesNotThrow(() -> Engine.ImmediateMode.start(engineSettings));
		Assertions.assertThrows(IllegalStateException.class, () -> Engine.ImmediateMode.start(engineSettings));
		Engine.ImmediateMode.shutdown("OK");
		Engine.ImmediateMode.update();
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testHandleRestarts(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(true);
		Assertions.assertNull(Engine.ImmediateMode.start(engineSettings));
		Engine.ImmediateMode.restart();
		Assertions.assertNull(Engine.ImmediateMode.update());
		Engine.ImmediateMode.shutdown("OK");
		Assertions.assertNotNull(Engine.ImmediateMode.update());
	}

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testHandleRestartsFalse(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		engineSettings.handleRestarts(false);
		Assertions.assertNull(Engine.ImmediateMode.start(engineSettings));
		Engine.ImmediateMode.restart();
		ShutdownReason reason = Engine.ImmediateMode.update();
		Assertions.assertInstanceOf(ShutdownReason.Restart.class, reason);
		Engine.ImmediateMode.start(((ShutdownReason.Restart) (reason)).engineSettings());
		Engine.ImmediateMode.shutdown("OK");
		Assertions.assertNotNull(Engine.ImmediateMode.update());
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(Engine.ImmediateMode.class);
	}
}
