package mylie.engine.input;

import mylie.engine.event.Event;

public record InputEvent<D extends InputDevice<D>, I extends Input<? super D, V>, V>(D device, I inputId,
		V value) implements Event {

	public InputEvent<D, I, V> with(D device, I inputId, V value) {
		return new InputEvent<>(device != null ? device : this.device, inputId != null ? inputId : this.inputId,
				value != null ? value : this.value);
	}
}
