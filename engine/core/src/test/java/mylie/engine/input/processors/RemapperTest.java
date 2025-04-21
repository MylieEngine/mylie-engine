package mylie.engine.input.processors;

import java.util.List;
import mylie.engine.core.async.Result;
import mylie.engine.input.*;
import mylie.engine.input.devices.Keyboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemapperTest {
	InputManager inputManager;
	DeviceRemapper remapper;
	Keyboard keyboard;
	@BeforeEach
	void setUp() {
		inputManager = InputManagerTest.inputManager();
		remapper = inputManager.processor(DeviceRemapper.class);
		keyboard = new Keyboard(new InputProvider() {
			@Override
			public <D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents() {
				return null;
			}
		}, false);
		// keyboard.value(InputDevice.Info.UUID,"ACTUAL",0);
	}

	@Test
	void testNoRemap() {
		InputManagerTest.update(inputManager);
		InputEvent<Keyboard, Keyboard.Key, Boolean> processed = remapper
				.process(new InputEvent<>(keyboard, Keyboard.Key.A, true), null);
		Assertions.assertEquals(processed.device(), keyboard);
		Assertions.assertNotEquals(processed.device(), inputManager.device(Keyboard.class, 0));
	}

	@Test
	void testRemap() {
		inputManager.mapDevice(Keyboard.class, 0, keyboard);
		InputManagerTest.update(inputManager);
		InputEvent<Keyboard, Keyboard.Key, Boolean> processed = remapper
				.process(new InputEvent<>(keyboard, Keyboard.Key.A, true), null);
		Assertions.assertNotEquals(processed.device(), keyboard);
		Assertions.assertEquals(processed.device(), inputManager.device(Keyboard.class, 0));
	}

	@Test
	void testUnmap() {
		inputManager.mapDevice(Keyboard.class, 0, keyboard);
		InputManagerTest.update(inputManager);
		InputEvent<Keyboard, Keyboard.Key, Boolean> processed = remapper
				.process(new InputEvent<>(keyboard, Keyboard.Key.A, true), null);
		Assertions.assertNotEquals(processed.device(), keyboard);
		Assertions.assertEquals(processed.device(), inputManager.device(Keyboard.class, 0));
		inputManager.mapDevice(Keyboard.class, 0, null);
		InputManagerTest.update(inputManager);
		processed = remapper.process(new InputEvent<>(keyboard, Keyboard.Key.A, true), null);
		Assertions.assertEquals(processed.device(), keyboard);
		Assertions.assertNotEquals(processed.device(), inputManager.device(Keyboard.class, 0));
	}

	@Test
	void testGetExistingDevice() {
		Assertions.assertNotNull(inputManager.device(Keyboard.class, 0));
	}

	@Test
	void testGetNonExistingDevice() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> inputManager.device(Keyboard.class, 1));
	}

	@Test
	void testGetNonExistingDeviceCategory() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> inputManager.device(NonRegisteredDevice.class, 0));
	}

	@Test
	void testMapVirtualDevice() {
		Keyboard keyboard1 = new Keyboard(null, true);
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> inputManager.mapDevice(Keyboard.class, 0, keyboard1));
	}

	private static class NonRegisteredDevice extends InputDevice<NonRegisteredDevice> {
		public NonRegisteredDevice(Class<NonRegisteredDevice> type, boolean isVirtual, InputProvider provider) {
			super(type, isVirtual, provider);
		}
	}
}
