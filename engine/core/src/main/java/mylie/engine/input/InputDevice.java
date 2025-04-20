package mylie.engine.input;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class InputDevice<D extends InputDevice<D>> {
	private final Map<Input<?, ?>, ?> states;
	private final Class<D> type;
	@Getter(AccessLevel.PACKAGE)
	private final boolean isVirtual;

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	private InputProvider provider;
	public InputDevice(Class<D> type, boolean isVirtual, InputProvider provider) {
		this.type = type;
		this.isVirtual = isVirtual;
		this.provider = provider;
		states = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <I extends Input<D, V>, V> V value(I input) {
		Object o = states.get(input);
		return o == null ? null : (V) o;
	}

	public <I extends Input<D, V>, V> boolean supports(I input) {
		return states.containsKey(input);
	}
}
