package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputProvider;
import mylie.engine.math.Vec2f;
@SuppressWarnings("unused")
public class Gamepad extends InputDevice<Gamepad> {
	public Gamepad(InputProvider provider, boolean virtual) {
		super(Gamepad.class, virtual, provider);
	}

	public static final Input<Gamepad, Integer> PLAYER_INDEX = () -> 0;

	public enum State implements GamepadInput<Boolean> {
		CONNECTED, WIRELESS;

		@Override
		public Boolean defaultValue() {
			return false;
		}
	}

	public enum Button implements GamepadInput<Boolean> {
		A, B, X, Y, LEFT_BUMPER, RIGHT_BUMPER, BACK, START, GUIDE, LEFT_STICK, RIGHT_STICK, DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,;

		@Override
		public Boolean defaultValue() {
			return false;
		}
	}

	public enum Axis implements GamepadInput<Float> {
		LEFT_X, LEFT_Y, RIGHT_X, RIGHT_Y, LEFT_TRIGGER, RIGHT_TRIGGER;

		@Override
		public Float defaultValue() {
			return 0.0f;
		}
	}

	public enum Pointer implements GamepadInput<Vec2f> {
		LEFT, RIGHT, DPAD;

		@Override
		public Vec2f defaultValue() {
			return Vec2f.of(0.0f, 0.0f);
		}
	}

	interface GamepadInput<V> extends Input<Gamepad, V> {

	}
}
