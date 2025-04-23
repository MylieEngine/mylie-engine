package mylie.engine.input.devices;

import mylie.engine.input.InputDeviceTest;
import org.junit.jupiter.api.Test;

class KeyboardTest {
	@Test
	public void testDefaultValues() {
		Keyboard keyboard = new Keyboard(null, false);
		InputDeviceTest.testDefaultValues(keyboard, Keyboard.Key.values(), false);
	}
}
