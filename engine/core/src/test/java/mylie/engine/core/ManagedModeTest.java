package mylie.engine.core;

import static mylie.engine.core.async.AsyncTestData.SCHEDULING_STRATEGIES_SOURCE;

import mylie.engine.TestUtils;
import mylie.engine.core.async.SchedulingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ManagedModeTest {

	@ParameterizedTest
	@MethodSource(SCHEDULING_STRATEGIES_SOURCE)
	public void testLifecycleCorrectness(SchedulingStrategy schedulingStrategy) {
		EngineSettings engineSettings = Platform.initialize(UnitTestPlatform.class);
		engineSettings.schedulingStrategy(schedulingStrategy);
		Run10.count = 0;
		Run10.restart = false;
		ShutdownReason shutdownReason = ManagedMode.start(engineSettings, Run10.class);
		ShutdownReason.Normal normal = Assertions.assertInstanceOf(ShutdownReason.Normal.class, shutdownReason);
		Assertions.assertEquals("OK", normal.reason());
		Assertions.assertTrue(Run10.restart);
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(ManagedMode.class);
	}

	public static class Run10 extends Application {
		static int count = 0;
		static boolean restart = false;
		public Run10(ComponentManager manager) {
			super(manager);
		}

		@Override
		protected void onInitialize() {
			Assertions.assertTrue(count == 10 || count == 0);
			if (count == 10) {
				restart = true;
			}
		}

		@Override
		protected void onUpdate(Time time) {
			count++;
			Assertions.assertTrue(count <= 20);
			if (count == 10) {
				Engine.restart();
			}
			if (count == 20) {
				Engine.shutdown("OK");
			}
		}

		@Override
		protected void onShutdown(ShutdownReason reason) {
			Assertions.assertTrue(count == 10 || count == 20);
		}
	}
}
