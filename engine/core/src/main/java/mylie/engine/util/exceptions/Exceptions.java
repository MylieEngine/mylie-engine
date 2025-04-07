package mylie.engine.util.exceptions;

public final class Exceptions {
	private Exceptions() {
		throw new IllegalInstantiationException(Exceptions.class);
	}

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
