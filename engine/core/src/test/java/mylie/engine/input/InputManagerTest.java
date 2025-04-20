package mylie.engine.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.Timer;
import mylie.engine.core.async.Result;
import mylie.engine.event.Event;
import mylie.engine.event.EventListener;
import mylie.engine.event.EventManager;
import mylie.engine.input.devices.Gamepad;
import mylie.engine.input.devices.Keyboard;
import mylie.engine.input.devices.Mouse;
import mylie.engine.input.processors.AxisDeathZone;
import mylie.engine.util.Versioned;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class InputManagerTest {
	InputManager inputManager;
	SimulatedInputProvider inputProvider = new SimulatedInputProvider();
	SimulatedInputListener inputListener = new SimulatedInputListener();
	@BeforeEach
	public void setup() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(Timer.class);
		componentManager.addComponent(EventManager.class);
		inputManager = componentManager.addComponent(InputManager.class);
		inputManager.registerInputProvider(inputProvider);
		componentManager.component(EventManager.class).registerListener(inputListener);
	}

	@Test
	void testGetDefaultDevices() {
		List<Keyboard> keyboards = inputManager.devices(Keyboard.class);
		Assertions.assertNotNull(keyboards);
		Assertions.assertEquals(1, keyboards.size());

		List<Mouse> mouses = inputManager.devices(Mouse.class);
		Assertions.assertNotNull(mouses);
		Assertions.assertEquals(1, mouses.size());

		List<Gamepad> gamepads = inputManager.devices(Gamepad.class);
		Assertions.assertNotNull(gamepads);
		Assertions.assertEquals(4, gamepads.size());

		Assertions.assertEquals(gamepads.getFirst(), inputManager.device(Gamepad.class, 0));
	}

	@Test
	void testFireEvent() {
		Versioned.Ref<Boolean> A_KEY = inputManager.device(Keyboard.class, 0).ref(Keyboard.Key.A);
		Assertions.assertFalse(A_KEY.value(true));
		inputProvider.addEvent(inputManager.device(Keyboard.class, 0), Keyboard.Key.A, true);
		inputManager.onUpdate();
		Assertions.assertEquals(true, A_KEY.value(true));
		Assertions.assertEquals(1, inputListener.events.size());
		Assertions.assertEquals(inputManager.device(Keyboard.class, 0), inputListener.events.getFirst().device());
		Assertions.assertEquals(Keyboard.Key.A, inputListener.events.getFirst().inputId());
		Assertions.assertEquals(true, inputListener.events.getFirst().value());
		Assertions.assertTrue(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
	}

	@Test
	void testFireEventWithNonMappedDevice() {
		Keyboard virtualKeyboard = inputManager.device(Keyboard.class, 0);
		Assertions.assertTrue(virtualKeyboard.isVirtual());
		inputProvider.addEvent(inputProvider.keyboard, Keyboard.Key.A, true);
		inputManager.onUpdate();
		Assertions.assertFalse(virtualKeyboard.value(Keyboard.Key.A));
		Assertions.assertEquals(1, inputListener.events.size());
		Assertions.assertEquals(inputProvider.keyboard, inputListener.events.getFirst().device());
		inputListener.events.clear();

		inputManager.mapDevice(Keyboard.class, 0, inputProvider.keyboard);
		Assertions.assertTrue(virtualKeyboard.value(InputDevice.State.MAPPED));
		Assertions.assertNotNull(virtualKeyboard.value(InputDevice.Info.NAME));
		Assertions.assertEquals("SIMULATED", virtualKeyboard.value(InputDevice.Info.NAME));
		Assertions.assertNotNull(virtualKeyboard.value(InputDevice.Info.UUID));
		Assertions.assertEquals("SIMULATED_UUID", virtualKeyboard.value(InputDevice.Info.UUID));
		inputListener.events.clear();

		inputProvider.addEvent(inputProvider.keyboard, Keyboard.Key.A, true);
		inputManager.onUpdate();
		Assertions.assertEquals(true, virtualKeyboard.value(Keyboard.Key.A));
		Assertions.assertEquals(1, inputListener.events.size());
		Assertions.assertEquals(virtualKeyboard, inputListener.events.getFirst().device());
		Assertions.assertEquals(Keyboard.Key.A, inputListener.events.getFirst().inputId());
		Assertions.assertEquals(true, inputListener.events.getFirst().value());
	}

	@Test
	void testFireInitialValueEvents() {
		inputProvider.addEvent(inputProvider.keyboard, Keyboard.Key.A, true);
		inputManager.onUpdate();
		Assertions.assertFalse(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		inputManager.mapDevice(Keyboard.class, 0, inputProvider.keyboard);
		Assertions.assertTrue(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
	}

	@Test
	void testRemoveMapping() {
		inputProvider.addEvent(inputProvider.keyboard, Keyboard.Key.A, true);
		inputManager.onUpdate();
		inputManager.mapDevice(Keyboard.class, 0, inputProvider.keyboard);
		Assertions.assertTrue(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		inputManager.mapDevice(Keyboard.class, 0, null);
		Assertions.assertFalse(inputManager.device(Keyboard.class, 0).value(Keyboard.Key.A));
		Assertions.assertFalse(inputManager.device(Keyboard.class, 0).value(InputDevice.State.MAPPED));
	}

	@Test
	void testUpdateDoesNotThrowException() {
		Assertions.assertDoesNotThrow(inputManager::onUpdate);
	}

	@Test
	void testRemoveProvider() {
		inputManager.unregisterInputProvider(inputProvider);
		inputProvider.addEvent(inputProvider.keyboard, Keyboard.Key.A, true);
		inputManager.onUpdate();
		Assertions.assertEquals(0, inputListener.events.size());
	}

	@Test
	void testAxisDeathZone() {
		float defaultValue = 1;
		float testValue = 0.05f;
		inputManager.mapDevice(Gamepad.class, 0, inputProvider.gamepad);
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, defaultValue);
		inputManager.onUpdate();
		Assertions.assertEquals(defaultValue, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, testValue);
		inputManager.onUpdate();
		Assertions.assertEquals(testValue, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
		AxisDeathZone axisDeathZone = new AxisDeathZone(0.1f);
		inputManager.registerInputProcessor(axisDeathZone);
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, defaultValue);
		inputManager.onUpdate();
		Assertions.assertEquals(defaultValue, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, testValue);
		inputManager.onUpdate();
		Assertions.assertEquals(0, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, defaultValue);
		inputManager.onUpdate();
		Assertions.assertEquals(defaultValue, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
		inputManager.unregisterInputProcessor(axisDeathZone);
		inputProvider.addEvent(inputProvider.gamepad, Gamepad.Axis.LEFT_Y, testValue);
		inputManager.onUpdate();
		Assertions.assertEquals(testValue, inputManager.device(Gamepad.class, 0).value(Gamepad.Axis.LEFT_Y));
	}

	@Test
	public void testInstantiation() {
		Assertions.assertNotNull(inputManager);
	}

	private static class SimulatedInputListener implements EventListener {
		List<InputEvent<?, ?, ?>> events = new LinkedList<>();
		@Override
		public void onEvent(Event event) {
			if (event instanceof InputEvent<?, ?, ?> inputEvent) {
				System.out.println(event.getClass().getSimpleName());
				this.events.add(inputEvent);
			}
		}
	}

	private static class SimulatedInputProvider implements InputProvider {
		private final Queue<InputEvent<?, ?, ?>> events = new LinkedList<>();
		private final Keyboard keyboard;
		private final Gamepad gamepad;

		public SimulatedInputProvider() {
			keyboard = new Keyboard(this, false);
			keyboard.value(InputDevice.Info.NAME, "SIMULATED", 0);
			keyboard.value(InputDevice.Info.UUID, "SIMULATED_UUID", 0);
			gamepad = new Gamepad(this, false);
			gamepad.value(InputDevice.Info.NAME, "SIMULATED_GAMEPAD", 0);
			gamepad.value(InputDevice.Info.UUID, "SIMULATED_GAMEPAD_UUID", 0);
		}

		@Override
		public List<InputDevice<?>> supportedInputDevices() {
			return List.of();
		}

		@Override
		public <D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents() {
			List<InputEvent<D, I, V>> copy = new LinkedList<>();
			while (!events.isEmpty()) {
				copy.add((InputEvent<D, I, V>) events.poll());
			}
			return Result.of(copy);
		}

		<D extends InputDevice<D>, I extends Input<D, V>, V> void addEvent(D device, I input, V value) {
			events.add(new InputEvent<>(device, input, value));
		}
	}
}
