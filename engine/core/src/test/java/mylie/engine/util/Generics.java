package mylie.engine.util;

import mylie.engine.input.InputDevice;

public class Generics {

	interface InputDevice<D extends InputDevice<D>> {
		<V> V value(Input<? super D, V> input);

		enum State implements Input<InputDevice<?>, Boolean> {
			COMMON, RELEASED, PRESSED;
		}

		interface Input<D extends InputDevice<?>, V> {

		}
	}

	interface Input<D extends InputDevice<D>, V> {
	}

	abstract class Keyboard implements InputDevice<Keyboard> {
		enum Key implements Input<Boolean> {
			A, B, C
		}
		interface Input<V> extends Generics.Input<Keyboard, V> {

		}
	}

	abstract class Mouse implements InputDevice<Mouse> {

		interface Input<V> extends Generics.Input<Mouse, V> {

		}
	}
}
