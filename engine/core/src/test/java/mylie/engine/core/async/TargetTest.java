package mylie.engine.core.async;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TargetTest {
	@Test
	public void testInstantiation() {
		Target target = new Target("Test");
		assertEquals("Test", target.name());
		assertTrue(target.bindable());
		assertFalse(target.managed());

		Target target2 = new Target("Test2", false);
		assertEquals("Test2", target2.name());
		assertTrue(target2.bindable());
		assertFalse(target2.managed());
	}

	@Test
	public void testDoubleBind() {
		Target target = new Target("Test");
		target.bind();
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, target::bind);
		assertEquals("Target < Test > is already bound to current thread", illegalStateException.getMessage());
		target.release();
	}

	@Test
	public void testUnbind() {
		Target target = new Target("Test");
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, target::release);
		assertEquals("Target < Test > is not bound to current thread", illegalStateException.getMessage());
		assertDoesNotThrow(target::bind);
		assertDoesNotThrow(target::release);
	}

	@Test
	public void testCurrent() {
		Target target = new Target("Test");
		assertFalse(target.current());
		assertDoesNotThrow(target::bind);
		assertTrue(target.current());
		assertDoesNotThrow(target::release);
		assertFalse(target.current());
	}

	@Test
	public void testNotBindable() {
		Target target = new Target("Test", true, false);
		assertFalse(target.bindable());
		assertTrue(target.managed());
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, target::bind);
		assertEquals("Target < Test > is not bindable", illegalStateException.getMessage());
		assertThrows(IllegalStateException.class, target::release);
		assertFalse(target.current());
	}

	@Test
	public void testBackgroundTarget() {
		Target target = Target.BACKGROUND;
		assertEquals("Background", target.name());
		assertTrue(target.managed());
		assertFalse(target.bindable());
	}
}
