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
		public <D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<?, ?, ?> process(
				InputEvent<?, ?, ?> event, Consumer<InputEvent<?, ?, ?>> additionalEvents) {
			InputEvent<D, I, V> eventCast = (InputEvent<D, I, V>) event;
			if (!eventCast.device().isVirtual()) {
				String uuid = eventCast.device().value(InputDevice.Info.UUID);
				List<D> devices = inputManager.devices(eventCast.device().type());
				for (D device : devices) {
					if (device.value(InputDevice.Info.UUID) != null) {
						if (device.value(InputDevice.Info.UUID).equals(uuid)) {
							return eventCast.with(device, null, null);
						}
					}
				}
			}
			return event;
		}
	}
}
