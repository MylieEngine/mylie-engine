package mylie.engine.core;

import mylie.engine.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmediateModeTest {

	@Test
	public void testLifecycle() {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
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

	@Test
	public void testLifecycleWithException() {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
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

	@Test
	public void testDoubleStart() {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		Assertions.assertDoesNotThrow(() -> Engine.ImmediateMode.start(engineSettings));
		Assertions.assertThrows(IllegalStateException.class, () -> Engine.ImmediateMode.start(engineSettings));
		Engine.ImmediateMode.shutdown("OK");
		Engine.ImmediateMode.update();
	}

	@Test
	public void testHandleRestarts() {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.handleRestarts(true);
		Assertions.assertNull(Engine.ImmediateMode.start(engineSettings));
		Engine.ImmediateMode.restart();
		Assertions.assertNull(Engine.ImmediateMode.update());
		Engine.ImmediateMode.shutdown("OK");
		Assertions.assertNotNull(Engine.ImmediateMode.update());
	}

	@Test
	public void testHandleRestartsFalse() {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
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
