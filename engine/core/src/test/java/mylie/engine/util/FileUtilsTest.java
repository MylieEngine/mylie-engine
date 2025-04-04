package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

	@Test
	void testLoadExistingProperties() {
		Properties props = FileUtils.loadPropertiesFromClasspath("/mylie/engine/version.properties");
		assertNotNull(props);
	}

	@Test
	void testLoadNonExistingProperties() {
		Exception e = assertThrows(RuntimeException.class,
				() -> FileUtils.loadPropertiesFromClasspath("/mylie/engine/version.properties.nonexisting"));
		assertTrue(e.getMessage()
				.contains("Failed to load properties from classpath: /mylie/engine/version.properties.nonexisting"));
	}
}
