package mylie.engine.input;

public interface Input<D extends InputDevice, V> {
	V defaultValue();
}
