package mylie.engine.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mylie.engine.core.async.Result;

public class ProvideNextFrame implements InputProvider {
	private final Queue<InputEvent<?, ?, ?>> events = new LinkedList<>();
	@SuppressWarnings("unchecked")
	@Override
	public <D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents() {
		List<InputEvent<D, I, V>> result = new LinkedList<>();
		while (!events.isEmpty()) {
			result.add((InputEvent<D, I, V>) events.poll());
		}
		return Result.of(result);
	}

	public void event(InputEvent<?, ?, ?> event) {
		events.add(event);
	}
}
