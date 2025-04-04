package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import mylie.engine.util.exceptions.FileNotFound;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

	@Test
	void testLoadExistingProperties() {
		Properties props = FileUtils.loadPropertiesFromClasspath("/mylie/engine/version.properties");
		assertNotNull(props);
	}

	@Test
	void testLoadNonExistingProperties() {
		String file = "/mylie/engine/version.properties.nonexisting";
		FileNotFound exception = assertThrows(FileNotFound.class, () -> FileUtils.loadPropertiesFromClasspath(file));
		String fileName = getFileName(exception);
		assertEquals(file, fileName);
	}

	private String getFileName(FileNotFound exception) {
		return exception.file();
	}
}
