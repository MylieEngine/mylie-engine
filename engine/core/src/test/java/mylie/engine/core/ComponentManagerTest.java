package mylie.engine.core;

import mylie.engine.util.exceptions.ConstructorNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentManagerTest {
	private ComponentManager manager;

	@BeforeEach
	void setUp() {
		manager = new ComponentManager();
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

	@Test
	void testRemoveNotExistingComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(ComponentA.class);
		Assertions.assertNotNull(manager.component(ComponentA.class));
		ComponentB componentB = manager.removeComponent(new ComponentB(manager));
		Assertions.assertNull(componentB);
	}

	@Test
	void testSubComponents() {
		manager.addComponent(ComponentC.class);
		Assertions.assertNotNull(manager.component(ComponentC.class));
		manager.component(ComponentC.class).test();
		manager.removeComponent(manager.component(ComponentC.class));
		Assertions.assertNull(manager.component(ComponentC.class));
		Assertions.assertNull(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentB.class));
	}

	@Test
	void testNoSuitableConstructor() {
		Assertions.assertThrows(ConstructorNotFoundException.class, () -> manager.addComponent(IllegalComponent.class));
	}

	public static class ComponentA extends Component {
		public ComponentA(ComponentManager manager) {
			super(manager);
		}
	}

	public static class ComponentB extends Component {
		public ComponentB(ComponentManager manager) {
			super(manager);
		}
	}

	public static class ComponentC extends Component {
		public ComponentC(ComponentManager manager) {
			super(manager);
		}

		@Override
		protected void onAdded() {
			super.onAdded();
			addComponent(ComponentA.class);
			addComponent(ComponentB.class);
		}

		protected void test() {
			Assertions.assertNotNull(component(ComponentA.class));
			Assertions.assertNotNull(component(ComponentA.class));
		}

		@Override
		protected void onRemoved() {
			super.onRemoved();
			removeComponent(component(ComponentA.class));
			removeComponent(component(ComponentB.class));
		}
	}

	public static class IllegalComponent extends Component {
		private final String name;
		public IllegalComponent(ComponentManager manager, String name) {
			super(manager);
			this.name = name;
		}

		public String name() {
			return name;
		}
	}
}
