package mylie.engine.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VersionedTest {

	@Test
	void testVersioned() {
		Versioned<Boolean> versioned = new Versioned<>(true);
		Assertions.assertTrue(versioned.value());
		Versioned.Ref<Boolean> ref = versioned.ref();
		Assertions.assertTrue(ref.isUpToDate());
		Assertions.assertTrue(ref.value());
		Assertions.assertEquals(versioned.frameId(), ref.frameId());
		versioned.value(false, 1);
		Assertions.assertNotEquals(versioned.frameId(), ref.frameId());
		Assertions.assertTrue(ref.value());
		Assertions.assertFalse(ref.isUpToDate());
		Assertions.assertTrue(ref.update());
		Assertions.assertEquals(versioned.frameId(), ref.frameId());
		Assertions.assertEquals(versioned.value(), ref.value());
	}

	@Test
	void testUpdateValue() {
		Versioned<Boolean> versioned = new Versioned<>(true);
		Versioned.Ref<Boolean> ref = versioned.ref();
		Assertions.assertFalse(ref.update());
		versioned.value(false, 1);
		Assertions.assertTrue(ref.value(false));
		Assertions.assertFalse(ref.value(true));
	}

	@Test
	void testUpdateFrameId() {
		Versioned<Boolean> versioned = new Versioned<>(true);
		Versioned.Ref<Boolean> ref = versioned.ref();
		Assertions.assertFalse(ref.update());
		versioned.value(false, 1);
		Assertions.assertNotEquals(versioned.frameId(), ref.frameId(false));
		Assertions.assertEquals(versioned.frameId(), ref.frameId(true));
	}

	@Test
	void testRefRef() {
		Versioned<Boolean> versioned = new Versioned<>(true);
		Versioned.Ref<Boolean> ref = versioned.ref();
		versioned.value(false, 1);
		Versioned.Ref<Boolean> ref1 = ref.ref();
		Assertions.assertEquals(ref.frameId(), ref1.frameId());
		Assertions.assertEquals(ref.value(), ref1.value());
		Assertions.assertNotEquals(ref1.value(), versioned.value());
		Assertions.assertEquals(ref.update(), ref1.update());
	}
}
