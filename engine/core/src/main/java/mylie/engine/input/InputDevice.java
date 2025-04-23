package mylie.engine.input;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.util.Versioned;

public class InputDevice<D extends InputDevice<D>> {
	final Input<InputDevice<?>, D> NATIVE_DEVICE = () -> null;
	final Input<InputDevice<?>, InputProvider> PROVIDER = () -> null;
	public enum State implements Input<InputDevice<?>, Boolean> {
		MAPPED, CONNECTED, VIRTUAL;

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
	private final Map<Input<? super D, ?>, Versioned<?>> states;
	@Getter(AccessLevel.PUBLIC)
	private final Class<D> type;
	public InputDevice(Class<D> type, boolean isVirtual, InputProvider provider) {
		this.type = type;
		states = new HashMap<>();
		value(State.VIRTUAL, isVirtual, 0);
		value(PROVIDER, provider, 0);
	}

	public <I extends Input<? super D, V>, V> V value(I input) {
		return versioned(input).value();
	}

	public <I extends Input<? super D, V>, V> Versioned.Ref<V> ref(I input) {
		return versioned(input).ref();
	}

	public <I extends Input<? super D, V>, V> void value(I input, V value, long frameId) {
		versioned(input).value(value, frameId);
	}

	@SuppressWarnings("unchecked")
	private <I extends Input<? super D, V>, V> Versioned<V> versioned(I input) {
		// noinspection unused
		return (Versioned<V>) states.computeIfAbsent(input, $ -> new Versioned<>(input.defaultValue()));
	}
}
