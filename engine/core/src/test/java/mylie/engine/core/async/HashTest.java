package mylie.engine.core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashTest {
	private static final String HELLO = "HELLO";
	@Test
	public void testHashEquals() {
		String args = HELLO;
		Hash hash = new Hash(AsyncTestData.SELF_LOCKING, args);
		Hash hash2 = new Hash(AsyncTestData.SELF_LOCKING, args);
		Assertions.assertEquals(hash, hash2);
	}

	@SuppressWarnings({"AssertBetweenInconvertibleTypes", "SimplifiableAssertion", "EqualsBetweenInconvertibleTypes"})
	@Test
	public void testHashNotEquals() {
		Hash hash = new Hash(AsyncTestData.SELF_LOCKING, HELLO);
		Hash hash2 = new Hash(AsyncTestData.SELF_LOCKING, "World");
		Assertions.assertNotEquals(hash, hash2);
		Assertions.assertNotEquals(HELLO, hash);
		Assertions.assertFalse(hash.equals(HELLO));
	}

	@Test
	public void testHashToString() {
		String args = HELLO;
		Hash hash = new Hash(AsyncTestData.SELF_LOCKING, args);
		Assertions.assertEquals("Hash< " + hash.hashCode() + " >", hash.toString());
	}

	@Test
	public void testCustomHash() {
		CustomHashObject object = new CustomHashObject(1);
		CustomHashObject object2 = new CustomHashObject(1);
		Hash hash = new Hash(AsyncTestData.SELF_LOCKING, object);
		Hash hash2 = new Hash(AsyncTestData.SELF_LOCKING, object2);
		Assertions.assertNotEquals(object, object2);
		Assertions.assertEquals(hash, hash2);
	}

	private static class CustomHashObject implements mylie.engine.core.async.CustomHash {
		final int value;
		CustomHashObject(int code) {
			this.value = code;
		}

		@Override
		public int customHash() {
			return value;
		}
	}
}
