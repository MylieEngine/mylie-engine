package mylie.engine.util.exceptions;

import java.io.Serial;
import java.util.Arrays;

/**
 * This exception is thrown when a requested constructor cannot be found for a given class and parameter types.
 * It is typically used in scenarios involving reflection-based instantiation of objects.
 */
public class ConstructorNotFoundException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 4646810972584243483L;

	/**
	 * Constructs a {@code ConstructorNotFoundException} with a detailed message.
	 *
	 * @param clazz          the class for which the constructor was not found
	 * @param parameterTypes the parameter types of the missing constructor
	 */
	public ConstructorNotFoundException(Class<?> clazz, Class<?>... parameterTypes) {
		super("Constructor not found for class " + clazz.getName() + " with parameters "
				+ Arrays.toString(parameterTypes));
	}
}
