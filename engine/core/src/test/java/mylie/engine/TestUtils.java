package mylie.engine;

import java.lang.reflect.Constructor;
import mylie.engine.util.exceptions.Exceptions;
import mylie.engine.util.exceptions.IllegalInstantiationException;
import org.junit.jupiter.api.Assertions;

public class TestUtils {
	private TestUtils() {

	}
	public static void testUtilityInstantiation(Class<?> clazz) {
		Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		Exception exception = Assertions.assertThrows(Exception.class, constructor::newInstance);
		IllegalInstantiationException instantiationException = (IllegalInstantiationException) Exceptions
				.getRootCause(exception);
		Assertions.assertEquals("Class " + clazz.getName() + " cannot be instantiated.",
				instantiationException.getMessage());
	}
}
