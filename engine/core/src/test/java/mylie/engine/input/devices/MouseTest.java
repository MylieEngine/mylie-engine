package mylie.engine.input.devices;

import mylie.engine.math.Vec2f;
import org.junit.jupiter.api.Test;

class MouseTest {
	@Test
	public void testDefaultValues() {
		Mouse mouse = new Mouse(null, false);
		DeviceTestUtils.testDefaultValues(mouse, Mouse.Axis.values(), 0.0f);
		DeviceTestUtils.testDefaultValues(mouse, Mouse.Button.values(), false);
		DeviceTestUtils.testDefaultValues(mouse, Mouse.Cursor.values(), Vec2f.of(0.0f, 0.0f));
	}
}
