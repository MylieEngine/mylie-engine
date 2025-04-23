package mylie.engine.input;

import mylie.engine.input.devices.Gamepad;
import mylie.engine.input.devices.Keyboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InputEventTest {
	@Test
	void testVariation() {
		Keyboard keyboard = new Keyboard(null, false);
		Keyboard keyboard1 = new Keyboard(null, false);
		InputEvent<Keyboard, Keyboard.Key, Boolean> event = new InputEvent<>(keyboard, Keyboard.Key.A, true);
		event = event.with(keyboard1, null, null);
		Assertions.assertEquals(keyboard1, event.device());
		Assertions.assertEquals(Keyboard.Key.A, event.inputId());
		event = event.with(null, Keyboard.Key.K, null);
		Assertions.assertEquals(Keyboard.Key.K, event.inputId());
		Assertions.assertTrue(event.value());
		event = event.with(null, null, false);
		Assertions.assertFalse(event.value());
	}
}
