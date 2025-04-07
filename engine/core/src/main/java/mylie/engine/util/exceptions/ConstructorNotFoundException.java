package mylie.engine.util.exceptions;

import java.io.Serial;
import java.util.Arrays;

public class ConstructorNotFoundException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 4646810972584243483L;
	public ConstructorNotFoundException(Class<?> clazz, Class<?>... parameterTypes) {
		super("Constructor not found for class " + clazz.getName() + " with parameters "
				+ Arrays.toString(parameterTypes));
	}
}
