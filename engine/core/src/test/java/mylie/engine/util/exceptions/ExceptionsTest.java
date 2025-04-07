package mylie.engine.util.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import mylie.engine.TestUtils;
import org.junit.jupiter.api.Test;

class ExceptionsTest {

	/**
	 * Test class for the `Exceptions` utility class, specifically for verifying the functionality
	 * of the `getRootCause` method. The `getRootCause` method identifies the root cause of a
	 * given Throwable by traversing the chain of causes.
	 */

	@Test
	void testGetRootCauseWithSingleException() {
		// Arrange
		Exception exception = new Exception("Root cause");

		// Act
		Throwable rootCause = Exceptions.getRootCause(exception);

		// Assert
		assertEquals(exception, rootCause);
		assertEquals("Root cause", rootCause.getMessage());
	}

	@Test
	void testGetRootCauseWithNestedExceptions() {
		// Arrange
		Throwable rootCause = new Exception("Root cause");
		Throwable middleCause = new Exception("Middle cause", rootCause);
		Throwable topException = new Exception("Top level exception", middleCause);

		// Act
		Throwable result = Exceptions.getRootCause(topException);

		// Assert
		assertEquals(rootCause, result);
		assertEquals("Root cause", result.getMessage());
	}

	@Test
	void testGetRootCauseWithMultipleLevelsOfCause() {
		// Arrange
		Throwable level3 = new Exception("Level 3");
		Throwable level2 = new Exception("Level 2", level3);
		Throwable level1 = new Exception("Level 1", level2);
		Throwable root = new Exception("Root", level1);

		// Act
		Throwable result = Exceptions.getRootCause(root);

		// Assert
		assertEquals(level3, result);
		assertEquals("Level 3", result.getMessage());
	}

	@Test
	void testGetRootCauseWithNull() {
		// Arrange, Act & Assert
		assertNull(Exceptions.getRootCause(null));
	}

	@Test
	void testGetRootCauseWhenNoCausePresent() {
		// Arrange
		Throwable exceptionWithNoCause = new Exception("No cause");

		// Act
		Throwable result = Exceptions.getRootCause(exceptionWithNoCause);

		// Assert
		assertEquals(exceptionWithNoCause, result);
		assertEquals("No cause", result.getMessage());
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(Exceptions.class);
	}
}
