package mylie.engine.input;

import java.util.List;
import mylie.engine.core.async.Result;

public interface InputProvider {
	<D extends InputDevice<D>, I extends Input<D, V>, V> Result<List<InputEvent<D, I, V>>> pollInputEvents();
}
