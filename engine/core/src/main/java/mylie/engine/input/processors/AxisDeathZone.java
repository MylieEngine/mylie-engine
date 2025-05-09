package mylie.engine.input.processors;

import java.util.function.Consumer;
import lombok.Setter;
import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputProcessor;
import mylie.engine.input.devices.Gamepad;

public final class AxisDeathZone implements InputProcessor {
	@Setter
	float threshold;

	public AxisDeathZone(float threshold) {
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<D, I, V> process(InputEvent<D, I, V> event,
			Consumer<InputEvent<D, I, V>> additionalEvents) {
		if (event.device().value(InputDevice.State.VIRTUAL) && event.inputId() instanceof Gamepad.Axis
				&& event.value() instanceof Float value && value < threshold && value > -threshold) {
			InputEvent<Gamepad, Gamepad.Axis, Float> axisEvent = (InputEvent<Gamepad, Gamepad.Axis, Float>) event;
			return (InputEvent<D, I, V>) axisEvent.with(null, null, 0.0f);
		}
		return event;
	}
}
