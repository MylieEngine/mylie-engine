package mylie.engine.util;

import mylie.engine.TestUtils;
import mylie.engine.util.exceptions.ConstructorNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassUtilsTest {
	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(ClassUtils.class);
	}

	@Test
	public void testConstructorNotFound() {
		Assertions.assertThrows(ConstructorNotFoundException.class, () -> ClassUtils.newInstance(PrivateClass.class));
	}

	private static class PrivateClass {
		private PrivateClass() {

		}
	}
}
