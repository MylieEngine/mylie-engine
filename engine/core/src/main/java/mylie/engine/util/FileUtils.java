package mylie.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import mylie.engine.util.exceptions.FileNotFound;

public final class FileUtils {
	private FileUtils() {
	}

	public static InputStream loadFile(String path) {
		return FileUtils.class.getResourceAsStream(path);
	}

	private static Properties loadProperties(InputStream inputStream) throws IOException {
		Properties props = new Properties();
		props.load(inputStream);
		return props;
	}

	public static Properties loadProperties(String path) {
		try (InputStream inputStream = loadFile(path)) {
			if (inputStream == null) {
				throw new IOException("File not found: " + path);
			}
			return loadProperties(inputStream);
		} catch (IOException e) {
			throw new FileNotFound(path, e);
		}
	}
}
