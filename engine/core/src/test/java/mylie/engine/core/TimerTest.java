package mylie.engine.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimerTest {

	@Test
	public void testWithoutSettings() {
		Timer timer = new Timer();
		Assertions.assertNotNull(timer);
	}

	@Test
	public void testTime() {
		Timer timer = new Timer();
		timer.onAdded();
		Assertions.assertEquals(0, timer.currentTime().frameId());
		timer.onUpdate();
		Assertions.assertEquals(1, timer.currentTime().frameId());
	}
}
