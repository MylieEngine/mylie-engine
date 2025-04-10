package mylie.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import mylie.engine.util.exceptions.FileNotFound;
import mylie.engine.util.exceptions.IllegalInstantiationException;

/**
 * A utility class for handling file and property loading.
 * <p>
 * This class provides static methods to load files and properties
 * from resource paths within the application. It is designed to prevent
 * instantiation.
 */
public final class FileUtils {
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 * Throws {@link IllegalInstantiationException} if instantiated via reflection.
	 *
	 * @throws IllegalInstantiationException if instantiation is attempted.
	 */
	private FileUtils() {
		throw new IllegalInstantiationException(FileUtils.class);
	}

	/**
	 * Loads a file as an {@link InputStream} from the given path.
	 * The path must refer to a resource in the classpath.
	 *
	 * @param path the path of the file to load as a resource.
	 * @return the {@code InputStream} to read the file's content, or {@code null} if the file is not found.
	 */
	public static InputStream loadFile(String path) {
		return FileUtils.class.getResourceAsStream(path);
	}

	/**
	 * Loads properties from an {@link InputStream}.
	 *
	 * @param inputStream the input stream to read the properties from.
	 * @return a {@link Properties} object containing the loaded properties.
	 * @throws IOException if an I/O error occurs while loading the properties.
	 */
	private static Properties loadProperties(InputStream inputStream) throws IOException {
		Properties props = new Properties();
		props.load(inputStream);
		return props;
	}

	/**
	 * Loads a properties file from the given path.
	 * The path must refer to a resource in the classpath.
	 *
	 * @param path the path of the properties file to load as a resource.
	 * @return a {@link Properties} object containing the loaded properties.
	 * @throws FileNotFound if the specified file is not found or cannot be loaded.
	 */
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
