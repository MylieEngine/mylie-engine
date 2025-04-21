package mylie.engine.input;

import mylie.engine.input.devices.Gamepad;
import mylie.engine.util.Versioned;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputDeviceTest {
	Gamepad gamepad;
	@BeforeEach
	void setUp() {
		gamepad = new Gamepad(null, true);
	}

	@Test
	void testValue() {
		Assertions.assertFalse(gamepad.value(Gamepad.Button.START));
		gamepad.value(Gamepad.Button.START, true, 0);
		Assertions.assertTrue(gamepad.value(Gamepad.Button.START));
	}

	@Test
	void testReference() {
		Assertions.assertFalse(gamepad.value(Gamepad.Button.START));
		Versioned.Ref<Boolean> ref = gamepad.ref(Gamepad.Button.START);
		Assertions.assertFalse(ref.value());
		gamepad.value(Gamepad.Button.START, true, 0);
		Assertions.assertFalse(ref.isUpToDate());
		Assertions.assertTrue(ref.value(true));
	}

	@Test
	void testDefaultValues() {
		testDefaultValues(gamepad, InputDevice.State.values(), false);
		testDefaultValues(gamepad, InputDevice.Info.values(), "");
	}

	@SuppressWarnings("unused")
	public static <D extends InputDevice<D>, I extends Input<D, V>, V> void testDefaultValues(D device,
			Input<? super D, V>[] inputs, V defaultValue) {
		for (Input<? super D, V> input : inputs) {
			Assertions.assertEquals(defaultValue, device.value(input));
		}
	}
}
