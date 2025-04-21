package mylie.engine.input;

import java.util.List;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.Timer;
import mylie.engine.core.async.Result;
import mylie.engine.event.EventManager;
import mylie.engine.input.devices.Keyboard;
import mylie.engine.input.processors.AxisDeathZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputManagerTest {
	InputManager inputManager;

	@BeforeEach
	public void setup() {
		inputManager = inputManager();
	}

	@Test
	void testProcessorAccess() {
		Assertions.assertNull(inputManager.processor(AxisDeathZone.class));
		inputManager.registerInputProcessor(new AxisDeathZone(0.5f));
		Assertions.assertNotNull(inputManager.processor(AxisDeathZone.class));
		inputManager.unregisterInputProcessor(inputManager.processor(AxisDeathZone.class));
		Assertions.assertNull(inputManager.processor(AxisDeathZone.class));
	}

	@Test
	void testProviderAccess() {
		Assertions.assertNull(inputManager.provider(NotRegisteredProvider.class));
		Assertions.assertNotNull(inputManager.provider(ProvideNextFrame.class));
		inputManager.unregisterInputProvider(inputManager.provider(ProvideNextFrame.class));
		Assertions.assertNull(inputManager.provider(ProvideNextFrame.class));
		inputManager.registerInputProvider(new ProvideNextFrame());
		Assertions.assertNotNull(inputManager.provider(ProvideNextFrame.class));
	}

	@Test
	void testDeviceQuery() {
		Assertions.assertTrue(inputManager.available(Keyboard.class, 0));
		Assertions.assertFalse(inputManager.available(Keyboard.class, 1));
		Assertions.assertFalse(inputManager.available(NotRegisteredDevice.class, 0));
	}

	@Test
	void testMapDeviceLifecycle() {
		Keyboard keyboard = new Keyboard(new InputProvider() {
			@Override
			public <D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents() {
				return null;
			}
		}, false);
		keyboard.value(InputDevice.Info.UUID, "ACTUAL", 0);
		keyboard.value(InputDevice.Info.NAME, "THE_NAME", 0);
		keyboard.value(Keyboard.Key.A, true, 0);
		keyboard.value(InputDevice.State.CONNECTED, true, 0);
		inputManager.mapDevice(Keyboard.class, 0, keyboard);
		Assertions.assertFalse(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		Assertions.assertEquals("", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.NAME));
		Assertions.assertEquals("", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.UUID));
		update(inputManager);
		Assertions.assertTrue(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		Assertions.assertEquals("THE_NAME", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.NAME));
		Assertions.assertEquals("ACTUAL", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.UUID));
		inputManager.mapDevice(Keyboard.class, 0, null);
		update(inputManager);
		Assertions.assertFalse(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		Assertions.assertEquals("", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.NAME));
		Assertions.assertEquals("", inputManager.device(Keyboard.class, 0).value(InputDevice.Info.UUID));
	}

	private static class NotRegisteredDevice extends InputDevice<NotRegisteredDevice> {
		public NotRegisteredDevice(Class<NotRegisteredDevice> type, boolean isVirtual, InputProvider provider) {
			super(type, isVirtual, provider);
		}
	}

	private static class NotRegisteredProvider implements InputProvider {
		@Override
		public <D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents() {
			return null;
		}
	}

	public static InputManager inputManager() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(Timer.class);
		componentManager.addComponent(EventManager.class);
		return componentManager.addComponent(InputManager.class);
	}

	public static void update(InputManager inputManager) {
		inputManager.onUpdate();
	}
}
