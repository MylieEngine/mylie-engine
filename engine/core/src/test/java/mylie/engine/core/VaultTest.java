package mylie.engine.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultTest {
	Vault vault;
	@BeforeEach
	public void setUp() {
		vault = new Vault(null);
	}

	@Test
	public void testAddItem() {
		Assertions.assertNull(vault.item(String.class));
		Assertions.assertDoesNotThrow(() -> vault.addItem("test"));
		Assertions.assertNotNull(vault.item(String.class));
		Assertions.assertEquals("test", vault.item(String.class));
		Assertions.assertNull(vault.item(Integer.class));
		Assertions.assertDoesNotThrow(() -> vault.removeItem("test"));
		Assertions.assertNull(vault.item(String.class));
	}
}
