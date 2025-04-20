package mylie.engine.input;

import lombok.Getter;
import mylie.engine.event.Event;

@Getter
public abstract class InputEvent<D extends InputDevice<D>, I extends Input<D, V>, V> implements Event {
	private final D device;
	private final I inputId;
	private final V value;

	public InputEvent(D device, I inputId, V value) {
		this.device = device;
		this.inputId = inputId;
		this.value = value;
	}
}
