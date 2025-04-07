package mylie.engine.util.exceptions;

import java.util.Arrays;

public class ConstructorNotFoundException extends RuntimeException {
	public ConstructorNotFoundException(Class<?> clazz, Class<?>... parameterTypes) {
		super("Constructor not found for class " + clazz.getName() + " with parameters "
				+ Arrays.toString(parameterTypes));
	}
}
