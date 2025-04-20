package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputProvider;
import mylie.engine.math.Vec2f;
@SuppressWarnings("unused")
public class Mouse extends InputDevice<Mouse> {
	public Mouse(InputProvider provider, boolean virtual) {
		super(Mouse.class, virtual, provider);
	}

	public enum Button implements MouseInput<Boolean> {
		LEFT, RIGHT, MIDDLE, BUTTON_4, BUTTON_5, BUTTON_6, BUTTON_7, BUTTON_8,;

		@Override
		public Boolean defaultValue() {
			return false;
		}
	}

	public enum Axis implements MouseInput<Float> {
		X, Y, WHEEL_X, WHEEL_Y,;

		@Override
		public Float defaultValue() {
			return 0.0f;
		}
	}

	public enum Cursor implements MouseInput<Vec2f> {
		Position, Movement;

		@Override
		public Vec2f defaultValue() {
			return Vec2f.of(0.0f, 0.0f);
		}
	}

	interface MouseInput<V> extends Input<Mouse, V> {

	}
}
