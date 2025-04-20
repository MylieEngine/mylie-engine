package mylie.engine.input;

import java.util.List;
import mylie.engine.core.async.Result;

public interface InputProvider {
	List<InputDevice<?>> supportedInputDevices();
	Result<List<InputEvent<?, ?, ?>>> pollInputEvents();
}
