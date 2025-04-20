package mylie.engine.input;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.Components;
import mylie.engine.core.Timer;
import mylie.engine.event.EventManager;
import mylie.engine.input.devices.Gamepad;
import mylie.engine.input.devices.Keyboard;
import mylie.engine.input.devices.Mouse;

public class InputManager extends Components.Core {
	private final List<InputProvider> inputProviders;
	private final List<InputProcessor> inputProcessors;
	private final Map<Class<? extends InputDevice<?>>, List<InputDevice<?>>> inputDevices;
	private Timer timer;
	EventManager eventManager;
	public InputManager(ComponentManager manager) {
		super(manager);
		this.inputProviders = new CopyOnWriteArrayList<>();
		this.inputProcessors = new CopyOnWriteArrayList<>();
		this.inputDevices = new ConcurrentHashMap<>();
		this.inputProcessors.add(new InputProcessors.ReMapper(this));
	}

	@Override
	protected void onAdded() {
		super.onAdded();
		eventManager = Objects.requireNonNull(component(EventManager.class));
		timer = component(Timer.class);
		devices(Keyboard.class).add(new Keyboard(null, true));
		devices(Mouse.class).add(new Mouse(null, true));
		for (int i = 0; i < 4; i++) {
			devices(Gamepad.class).add(new Gamepad(null, true));
		}
	}

	public <T extends InputDevice<T>> void mapDevice(Class<T> type, int id, T device) {
		if (device == null) {
			disableMapping(device(type, id));
		} else if (device.isVirtual()) {
			throw new IllegalArgumentException("Cannot map a virtual device");
		} else {
			enableMapping(device(type, id), device);
		}
	}

	private <T extends InputDevice<T>> void enableMapping(T virtualDevice, T actualDevice) {
		virtualDevice.provider(actualDevice.provider());
		processInputEvent(new InputEvent<>(virtualDevice, InputDevice.State.MAPPED, true));
		for (InputDevice.Info value : InputDevice.Info.values()) {
			processInputEvent(new InputEvent<>(virtualDevice, value, actualDevice.value(value)));
		}
		for (Input<?, ?> input : actualDevice.states().keySet()) {
			if (input instanceof InputDevice.Info || input instanceof InputDevice.State) {
				continue;
			}
			Input<T, ?> castedInput = (Input<T, ?>) input;
			processInputEvent(new InputEvent(virtualDevice, castedInput, actualDevice.value(castedInput)));
		}
	}

	private <T extends InputDevice<T>> void disableMapping(T virtualDevice) {
		virtualDevice.provider(null);
		processInputEvent(new InputEvent<>(virtualDevice, InputDevice.State.MAPPED, false));
		for (InputDevice.Info value : InputDevice.Info.values()) {
			processInputEvent(new InputEvent<>(virtualDevice, value, value.defaultValue()));
		}
		virtualDevice.states().forEach((input, state) -> {
			if (input instanceof InputDevice.Info || input instanceof InputDevice.State) {
				return;
			}
			Input<T, ?> castedInput = (Input<T, ?>) input;
			processInputEvent(new InputEvent(virtualDevice, castedInput, castedInput.defaultValue()));
		});
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		pollInputProviders();
	}

	@SuppressWarnings("unchecked")
	private <D extends InputDevice<D>, I extends Input<D, V>, V> void processInputEvent(InputEvent<?, ?, ?> event) {
		for (InputProcessor inputProcessor : inputProcessors) {
			event = inputProcessor.process(event, this::processInputEvent);
		}
		InputEvent<D, I, V> castedEvent = (InputEvent<D, I, V>) event;
		D device = castedEvent.device();
		device.value(castedEvent.inputId(), castedEvent.value(), timer == null ? 0 : timer.currentTime().frameId());
		eventManager.fireEvent(event);
	}

	private void processInputEvents(List<InputEvent<?, ?, ?>> events) {
		System.out.println(events.size() + " events to process");
		for (InputEvent<?, ?, ?> event : events) {
			processInputEvent(event);
		}
	}

	@SuppressWarnings("unchecked")
	private void pollInputProviders() {
		CompletableFuture<Void>[] futures = new CompletableFuture[inputProviders.size()];
		for (int i = 0; i < inputProviders.size(); i++) {
			futures[i] = inputProviders.get(i).pollInputEvents().thenAccept(this::processInputEvents)
					.toCompletableFuture();
		}
		CompletableFuture.allOf(futures).join();
	}

	@SuppressWarnings("unchecked")
	public <T extends InputDevice<T>> List<T> devices(Class<T> type) {
		return (List<T>) inputDevices.computeIfAbsent(type, _ -> new CopyOnWriteArrayList<>());
	}

	public <T extends InputDevice<T>> T device(Class<T> type, int id) {
		List<T> deviceList = devices(type);
		if (deviceList.isEmpty()) {
			throw new IllegalArgumentException("No devices of type " + type.getSimpleName() + " registered");
		} else if (deviceList.size() <= id) {
			throw new IllegalArgumentException(
					"No device with id " + id + " registered for type " + type.getSimpleName());
		}
		return deviceList.get(id);
	}

	public void registerInputProvider(InputProvider provider) {
		inputProviders.add(provider);
	}

	public void unregisterInputProvider(InputProvider provider) {
		inputProviders.remove(provider);
	}

	public void registerInputProcessor(InputProcessor processor) {
		inputProcessors.add(processor);
	}

	public void unregisterInputProcessor(InputProcessor processor) {
		inputProcessors.remove(processor);
	}
}
