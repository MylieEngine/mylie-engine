package mylie.engine.input.processors;

import java.util.function.Consumer;
import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputProcessor;
import mylie.engine.input.devices.Gamepad;

public final class AxisDeathZone implements InputProcessor {
    float threshold;

    public AxisDeathZone(float threshold) {
        this.threshold = threshold;
    }

    @Override
	public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<?, ?, ?> process(InputEvent<?, ?, ?> event,
			Consumer<InputEvent<?, ?, ?>> additionalEvents) {
        if(event.device().isVirtual()){
            if(event.inputId() instanceof Gamepad.Axis axis){
                InputEvent<Gamepad, Gamepad.Axis, Float> eventCast = (InputEvent<Gamepad, Gamepad.Axis, Float>) event;
                if(eventCast.value() < threshold && eventCast.value() > -threshold){
                    return eventCast.with(eventCast.device(), null, 0.0f);
                }
            }
        }
        return event;
	}
}
