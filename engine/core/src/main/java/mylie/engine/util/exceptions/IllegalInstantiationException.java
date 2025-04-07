package mylie.engine.util.exceptions;

import lombok.Getter;

@Getter
public class IllegalInstantiationException extends RuntimeException {
	private final Class<?> clazz;

	public IllegalInstantiationException(Class<?> clazz) {
		super("Class " + clazz.getName() + " cannot be instantiated.");
		this.clazz = clazz;
	}
}
