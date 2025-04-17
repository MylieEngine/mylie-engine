package mylie.engine.core;

import mylie.engine.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EngineStartTest {

	/*
	 * @Test public void initializePlatform() { EngineSettings engineSettings =
	 * Platform.initialize(UnitTestPlatform.class);
	 * Assertions.assertNotNull(engineSettings); Assertions.assertDoesNotThrow(() ->
	 * Engine.initialize(engineSettings));
	 * Assertions.assertDoesNotThrow(Engine::update);
	 * Assertions.assertDoesNotThrow(Engine::destroy); }
	 */

	@Test
	public void testNotStarted(){
		Assertions.assertThrows(IllegalStateException.class, Engine::shutdownReason);
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(Engine.class);
	}
}
