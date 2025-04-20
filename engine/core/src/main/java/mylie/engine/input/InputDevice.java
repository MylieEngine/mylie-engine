package mylie.engine.input;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.util.Versioned;

public class InputDevice<D extends InputDevice<D>> {
	public enum State implements Input<InputDevice<?>, Boolean> {
		MAPPED;

		@Override
		public Boolean defaultValue() {
			return false;
		}
	}

	public enum Info implements Input<InputDevice<?>, String> {
		NAME, UUID;

		@Override
		public String defaultValue() {
			return "";
		}
	}

	@Getter(AccessLevel.PACKAGE)
	private final Map<Input<?, ?>, Versioned<?>> states;
	@Getter(AccessLevel.PUBLIC)
	private final Class<D> type;
	@Getter(AccessLevel.PUBLIC)
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

	public <I extends Input<? super D, V>, V> V value(I input) {
		return versioned(input).value();
	}

	public <I extends Input<? super D, V>, V> Versioned.Ref<V> ref(I input) {
		return versioned(input).ref();
	}

	public <I extends Input<D, V>, V> boolean supports(I input) {
		return states.containsKey(input);
	}

	public <I extends Input<? super D, V>, V> void value(I input, V value, long frameId) {
		versioned(input).value(value, frameId);
	}

	@SuppressWarnings("unchecked")
	private <I extends Input<? super D, V>, V> Versioned<V> versioned(I input) {
		return (Versioned<V>) states.computeIfAbsent(input, _ -> new Versioned<>(input.defaultValue()));
	}
}
