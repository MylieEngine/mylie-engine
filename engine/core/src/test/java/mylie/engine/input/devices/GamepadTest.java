package mylie.engine.input.devices;

import mylie.engine.math.Vec2f;
import org.junit.jupiter.api.Test;

class GamepadTest {
	@Test
	public void testDefaultValues() {
		Gamepad gamepad = new Gamepad(null, false);
		DeviceTestUtils.testDefaultValues(gamepad, Gamepad.Axis.values(), 0.0f);
		DeviceTestUtils.testDefaultValues(gamepad, Gamepad.Button.values(), false);
		DeviceTestUtils.testDefaultValues(gamepad, Gamepad.Pointer.values(), Vec2f.of(0.0f, 0.0f));
		DeviceTestUtils.testDefaultValues(gamepad, Gamepad.State.values(), false);
	}
}
