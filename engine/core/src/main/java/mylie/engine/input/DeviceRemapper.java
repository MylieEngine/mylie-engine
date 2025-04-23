package mylie.engine.input;

import java.util.List;
import java.util.function.Consumer;

public class DeviceRemapper implements InputProcessor {
	private final InputManager inputManager;

	public DeviceRemapper(InputManager inputManager) {
		this.inputManager = inputManager;
	}

	@Override
	public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<D, I, V> process(InputEvent<D, I, V> event,
			Consumer<InputEvent<D, I, V>> additionalEvents) {
		boolean isVirtual = event.device().value(InputDevice.State.VIRTUAL);
		if (!isVirtual) {
			String uuid = event.device().value(InputDevice.Info.UUID);
			List<D> devices = inputManager.devices(event.device().type());
			for (D device : devices) {
				if (device.value(device.PROVIDER) == event.device().value(event.device().PROVIDER)
						&& device.value(InputDevice.Info.UUID) != null
						&& device.value(InputDevice.Info.UUID).equals(uuid)) {
					return event.with(device, null, null);
				}
			}
		}
		return event;
	}
}
