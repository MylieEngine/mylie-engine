package mylie.engine.util.exceptions;

import java.io.Serial;
import lombok.Getter;

@Getter
public class FileNotFound extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 2387348580037318229L;
	private final String file;
	public FileNotFound(String file, Throwable cause) {
		super("Failed to load file: " + file, cause);
		this.file = file;
	}
}
