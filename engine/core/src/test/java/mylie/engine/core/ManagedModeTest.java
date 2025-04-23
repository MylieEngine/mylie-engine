package mylie.engine.core;

import static mylie.engine.core.async.AsyncTestData.SCHEDULING_STRATEGIES_SOURCE;

import java.util.Objects;
import mylie.engine.TestUtils;
import mylie.engine.core.async.Scheduler;
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
		Run10.count2 = 0;
		Run10.restart = false;
		ShutdownReason shutdownReason = ManagedMode.start(engineSettings, new Run10());
		ShutdownReason.Normal normal = Assertions.assertInstanceOf(ShutdownReason.Normal.class, shutdownReason);
		Assertions.assertEquals("OK", normal.reason());
		Assertions.assertTrue(Run10.restart);
		Assertions.assertEquals(Run10.SHUTDOWN_COUNT, Run10.count2);
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(ManagedMode.class);
	}

	public static class Run10 extends Application {
		private static int count;
		private static boolean restart;
		private static final int INITIAL_COUNT = 0;
		private static final int RESTART_COUNT = 10;
		private static final int SHUTDOWN_COUNT = 20;
		private static int count2;
		public Run10() {

		}

		@Override
		protected void onInitialize() {
			Assertions.assertTrue(count == RESTART_COUNT || count == INITIAL_COUNT);
			if (count == RESTART_COUNT) {
				restart = true;
			}
		}

		@Override
		protected void onUpdate(Time time) {
			count++;
			Assertions.assertTrue(count <= SHUTDOWN_COUNT);
			if (count == RESTART_COUNT) {
				Engine.restart();
			}
			if (count == SHUTDOWN_COUNT) {
				Engine.shutdown("OK");
			}
			Objects.requireNonNull(component(Scheduler.class)).submit(() -> count2++, Engine.TARGET);
		}

		@Override
		protected void onShutdown(ShutdownReason reason) {
			Assertions.assertTrue(count == RESTART_COUNT || count == SHUTDOWN_COUNT);
		}
	}
}
