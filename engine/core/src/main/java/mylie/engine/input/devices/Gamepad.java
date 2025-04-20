package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputProvider;
import mylie.engine.math.Vec2f;

public class Gamepad extends InputDevice<Gamepad> {
	public Gamepad(InputProvider provider, boolean virtual) {
		super(Gamepad.class, virtual, provider);
	}

	public enum State implements GamepadInput<State> {
		CONNECTED, WIRELESS
	}

	public enum Button implements GamepadInput<Boolean> {
		A, B, X, Y, LEFT_BUMPER, RIGHT_BUMPER, BACK, START, GUIDE, LEFT_STICK, RIGHT_STICK, DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
	}

	public enum Axis implements GamepadInput<Float> {
		LEFT_X, LEFT_Y, RIGHT_X, RIGHT_Y, LEFT_TRIGGER, RIGHT_TRIGGER
	}

	public enum Pointer implements GamepadInput<Vec2f> {
		LEFT, RIGHT, DPAD
	}

	interface GamepadInput<V> extends Input<Gamepad, V> {

	}

	public abstract static class Event<I extends GamepadInput<V>, V> extends InputEvent<Gamepad, I, V> {
		public Event(Gamepad device, I inputId, V value) {
			super(device, inputId, value);
		}
	}

	public static final class ButtonEvent extends Event<Button, Boolean> {
		public ButtonEvent(Gamepad device, Button inputId, Boolean value) {
			super(device, inputId, value);
		}
	}

	public static final class AxisEvent extends Event<Axis, Float> {
		public AxisEvent(Gamepad device, Axis inputId, Float value) {
			super(device, inputId, value);
		}
	}

	public static final class PointerEvent extends Event<Pointer, Vec2f> {
		public PointerEvent(Gamepad device, Pointer inputId, Vec2f value) {
			super(device, inputId, value);
		}
	}

	public static final class StateEvent extends Event<State, State> {
		public StateEvent(Gamepad device, State inputId, State value) {
			super(device, inputId, value);
		}
	}
}
