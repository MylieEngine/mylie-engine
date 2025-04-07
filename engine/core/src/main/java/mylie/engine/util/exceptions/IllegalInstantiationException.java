package mylie.engine.util.exceptions;

import java.io.Serial;
import lombok.Getter;

@Getter
public class IllegalInstantiationException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 2108472316156304685L;

	private final Class<?> clazz;

	public IllegalInstantiationException(Class<?> clazz) {
		super("Class " + clazz.getName() + " cannot be instantiated.");
		this.clazz = clazz;
	}
}
