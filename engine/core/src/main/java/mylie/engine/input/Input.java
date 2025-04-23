package mylie.engine.input;

@SuppressWarnings("unused")
public interface Input<D extends InputDevice<?>, V> {
	V defaultValue();
}
