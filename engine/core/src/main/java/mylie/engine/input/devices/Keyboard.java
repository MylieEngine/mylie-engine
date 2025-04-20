package mylie.engine.input.devices;

import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputProvider;

@SuppressWarnings("unused")
public class Keyboard extends InputDevice<Keyboard> {

	public Keyboard(InputProvider provider, boolean virtual) {
		super(Keyboard.class, virtual, provider);
	}

	public enum Key implements KeyboardInput<Boolean> {
		F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9, GRAVE, MINUS, EQUALS, BACKSLASH, BACKSPACE, TAB, LEFT_BRACKET, RIGHT_BRACKET, CAPS_LOCK, SEMICOLON, APOSTROPHE, ENTER, LEFT_SHIFT, COMMA, PERIOD, SLASH, RIGHT_SHIFT, LEFT_CONTROL, LEFT_ALT, SPACE, RIGHT_ALT, RIGHT_CONTROL, INSERT, HOME, PAGE_UP, DELETE, END, PAGE_DOWN, UP, LEFT, DOWN, RIGHT, NUM_LOCK, NUMPAD_DIVIDE, NUMPAD_MULTIPLY, NUMPAD_SUBTRACT, NUMPAD_7, NUMPAD_8, NUMPAD_9, NUMPAD_ADD, NUMPAD_4, NUMPAD_5, NUMPAD_6, NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_ENTER, NUMPAD_0, NUMPAD_DECIMAL, ESCAPE, PRINT_SCREEN, SCROLL_LOCK, PAUSE, LEFT_SUPER, RIGHT_SUPER, MENU
	}

	interface KeyboardInput<V> extends Input<Keyboard, V> {

	}

	public abstract static class Event<I extends KeyboardInput<V>, V> extends InputEvent<Keyboard, I, V> {
		public Event(Keyboard source, I id, V value) {
			super(source, id, value);
		}
	}

	public static final class KeyEvent extends Event<Key, Boolean> {
		public KeyEvent(Keyboard source, Key id, Boolean value) {
			super(source, id, value);
		}
	}
}
