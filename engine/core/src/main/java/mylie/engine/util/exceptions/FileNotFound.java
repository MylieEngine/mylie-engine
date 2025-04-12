package mylie.engine.util.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * Exception thrown when a specified file cannot be found or loaded.
 * <p>
 * This exception provides the path of the file that failed to load and the underlying cause of the failure.
 * </p>
 *
 * @since 1.0
 */
@Getter
public class FileNotFound extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 2387348580037318229L;

	/**
	 * The path of the file that could not be found or loaded.
	 */
	private final String file;

	/**
	 * Constructs a new {@code FileNotFound} exception with the specified file path
	 * and cause.
	 *
	 * @param file  the path of the file that could not be found or loaded
	 * @param cause the cause of the exception (may be {@code null})
	 */
	public FileNotFound(String file, Throwable cause) {
		super("Failed to load file: " + file, cause);
		this.file = file;
	}
}
