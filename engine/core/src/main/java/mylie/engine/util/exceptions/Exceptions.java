package mylie.engine.util.exceptions;

/**
 * Utility class providing methods for working with exceptions.
 * <p>
 * This class offers static helper methods to analyze exception hierarchies and extract valuable information.
 * It is not intended to be instantiated.
 */
public final class Exceptions {
	private Exceptions() {
		throw new IllegalInstantiationException(Exceptions.class);
	}

	/**
	 * Returns the root cause of the given exception.
	 * <p>
	 * Traverses the chain of causes to find the original root cause of the exception.
	 *
	 * @param t the throwable whose root cause is to be identified; may be {@code null}
	 * @return the root cause of the throwable, or {@code null} if the input is {@code null}
	 */
	public static Throwable getRootCause(Throwable t) {
		if (t == null)
			return null;
		Throwable cause = t;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}
}
