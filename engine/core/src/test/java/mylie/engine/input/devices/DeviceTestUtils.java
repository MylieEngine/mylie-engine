package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import org.junit.jupiter.api.Assertions;

public final class DeviceTestUtils {
	private DeviceTestUtils() {
	}
	@SuppressWarnings("unused")
	public static <D extends InputDevice<D>, I extends Input<D, V>, V> void testDefaultValues(D device,
			Input<D, V>[] inputs, V defaultValue) {
		for (Input<D, V> input : inputs) {
			Assertions.assertEquals(defaultValue, device.value(input));
		}
	}
}
