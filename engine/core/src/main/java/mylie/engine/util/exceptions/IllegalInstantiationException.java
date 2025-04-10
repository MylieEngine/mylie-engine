package mylie.engine.util.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * Exception thrown when an attempt is made to instantiate a class
 * that cannot or should not be instantiated.
 * <p>
 * This is typically used to enforce restrictions on instantiating utility
 * or singleton classes.
 */
@Getter
public class IllegalInstantiationException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 2108472316156304685L;

	private final Class<?> clazz;

	/**
	 * Constructs a new {@code IllegalInstantiationException} for the specified class.
	 *
	 * @param clazz The class that was attempted to be instantiated.
	 */
	public IllegalInstantiationException(Class<?> clazz) {
		super("Class " + clazz.getName() + " cannot be instantiated.");
		this.clazz = clazz;
	}
}
