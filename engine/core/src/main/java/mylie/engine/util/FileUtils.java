package mylie.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import mylie.engine.util.exceptions.FileNotFound;

public final class FileUtils {
	private FileUtils() {
	}

	public static InputStream loadFromClasspath(String path) {
		return FileUtils.class.getResourceAsStream(path);
	}

	public static Properties loadPropertiesFromClasspath(String path) {
		Properties props = new Properties();
		try (InputStream is = loadFromClasspath(path)) {
			if (is == null) {
				throw new FileNotFound(path, null);
			}
			props.load(is);
		} catch (IOException ioException) {
			throw new FileNotFound(path, ioException);
		}
		return props;
	}
}
