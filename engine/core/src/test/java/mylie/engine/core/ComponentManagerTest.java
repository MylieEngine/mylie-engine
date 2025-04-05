package mylie.engine.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentManagerTest {
	private ComponentManager manager;

	@BeforeEach
	void setUp() {
		manager = new ComponentManager();
	}

	@AfterEach
	void tearDown() {
		manager = null;
	}

	@Test
	void testAddComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertNotNull(manager.component(ComponentA.class));
	}

	@Test
	void testGetComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertSame(manager.component(ComponentA.class), manager.component(ComponentA.class));
	}

	@Test
	void testGetNotExisitingComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertNotNull(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentB.class));
	}

	@Test
	void testRemoveComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertNotNull(manager.component(ComponentA.class));
		manager.removeComponent(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentA.class));
	}

	void testRemoveNotExistingComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertNotNull(manager.component(ComponentA.class));
		ComponentB componentB = manager.removeComponent(new ComponentB(manager));
		Assertions.assertNull(componentB);
	}

	private static class ComponentA extends Component {
		public ComponentA(ComponentManager manager) {
			super(manager);
		}
	}

	private static class ComponentB extends Component {
		public ComponentB(ComponentManager manager) {
			super(manager);
		}
	}
}
