package mylie.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BuildTest {
	@Test
	public void testAllFields() {
		Assertions.assertNotNull(Build.buildTime());
		Assertions.assertNotNull(Build.engineVersion());
		Assertions.assertNotNull(Build.lastTag());
		Assertions.assertNotNull(Build.commitDistance());
		Assertions.assertNotNull(Build.gitHash());
		Assertions.assertNotNull(Build.gitHashFull());
		Assertions.assertNotNull(Build.branchName());
		Assertions.assertNotNull(Build.isCleanTag());
	}

	@Test
	public void testInstantiation() {
		TestUtils.testUtilityInstantiation(Build.class);
	}
}
