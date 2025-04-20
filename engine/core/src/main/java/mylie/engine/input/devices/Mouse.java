package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputProvider;
import mylie.engine.math.Vec2f;
@SuppressWarnings("unused")
public class Mouse extends InputDevice<Mouse> {
	public Mouse(InputProvider provider, boolean virtual) {
		super(Mouse.class, virtual, provider);
	}

	public enum Button implements MouseInput<Boolean> {
		LEFT, RIGHT, MIDDLE, BUTTON_4, BUTTON_5, BUTTON_6, BUTTON_7, BUTTON_8,
	}

	public enum Axis implements MouseInput<Float> {
		X, Y, WHEEL_X, WHEEL_Y,
	}

	public enum Cursor implements MouseInput<Vec2f> {
		Position, Movement
	}

	interface MouseInput<V> extends Input<Mouse, V> {

	}

	public abstract static class Event<I extends MouseInput<V>, V> extends InputEvent<Mouse, I, V> {
		public Event(Mouse device, I inputId, V value) {
			super(device, inputId, value);
		}
	}

	public static final class ButtonEvent extends Event<Button, Boolean> {
		public ButtonEvent(Mouse device, Button inputId, Boolean value) {
			super(device, inputId, value);
		}
	}

	public static final class AxisEvent extends Event<Axis, Float> {
		public AxisEvent(Mouse device, Axis inputId, Float value) {
			super(device, inputId, value);
		}
	}

	public static final class CursorEvent extends Event<Cursor, Vec2f> {
		public CursorEvent(Mouse device, Cursor inputId, Vec2f value) {
			super(device, inputId, value);
		}
	}
}
