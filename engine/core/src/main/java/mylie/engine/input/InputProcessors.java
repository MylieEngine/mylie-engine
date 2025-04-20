package mylie.engine.input;

import java.util.List;
import java.util.function.Consumer;

class InputProcessors {

	static class ReMapper implements InputProcessor {
		private final InputManager inputManager;

		public ReMapper(InputManager inputManager) {
			this.inputManager = inputManager;
		}

		@Override
		public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<D, I, V> process(
				InputEvent<D, I, V> event, Consumer<InputEvent<D, I, V>> additionalEvents) {
			if (!event.device().isVirtual()) {
				String uuid = event.device().value(InputDevice.Info.UUID);
				List<D> devices = inputManager.devices(event.device().type());
				for (D device : devices) {
					if (device.value(InputDevice.Info.UUID) != null) {
						if (device.value(InputDevice.Info.UUID).equals(uuid)) {
							return event.with(device, null, null);
						}
					}
				}
			}
			return event;
		}
	}
}
