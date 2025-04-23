package mylie.engine.input.devices;

import mylie.engine.input.InputDeviceTest;
import mylie.engine.math.Vec2f;
import org.junit.jupiter.api.Test;

class GamepadTest {
	@Test
	public void testDefaultValues() {
		Gamepad gamepad = new Gamepad(null, false);
		InputDeviceTest.testDefaultValues(gamepad, Gamepad.Axis.values(), 0.0f);
		InputDeviceTest.testDefaultValues(gamepad, Gamepad.Button.values(), false);
		InputDeviceTest.testDefaultValues(gamepad, Gamepad.Pointer.values(), Vec2f.of(0.0f, 0.0f));
		InputDeviceTest.testDefaultValues(gamepad, Gamepad.State.values(), false);
	}
}
