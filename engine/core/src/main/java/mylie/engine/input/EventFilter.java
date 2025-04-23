package mylie.engine.input;

import java.util.function.Consumer;

public class EventFilter implements InputProcessor {
	@Override
	public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<D, I, V> process(InputEvent<D, I, V> event,
			Consumer<InputEvent<D, I, V>> additionalEvents) {
		if (event.device().isVirtual() || event.inputId() == InputDevice.State.CONNECTED) {
			return event;
		}
		return null;
	}
}
