package mylie.engine.util.exceptions;

import lombok.Getter;

@Getter
public class FileNotFound extends RuntimeException {
	private final String file;
	public FileNotFound(String file, Throwable cause) {
		super("Failed to load file: " + file, cause);
		this.file = file;
	}
}
