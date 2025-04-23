package mylie.engine.core;

import mylie.engine.TestUtils;
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
		manager.addComponent(new ComponentA());
		Assertions.assertNotNull(manager.component(ComponentA.class));
	}

	@Test
	void testGetComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(new ComponentA());
		Assertions.assertSame(manager.component(ComponentA.class), manager.component(ComponentA.class));
	}

	@Test
	void testGetNotExisitingComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(new ComponentA());
		Assertions.assertNotNull(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentB.class));
	}

	@Test
	void testRemoveComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(new ComponentA());
		Assertions.assertNotNull(manager.component(ComponentA.class));
		manager.removeComponent(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentA.class));
	}

	@Test
	void testRemoveNotExistingComponent() {
		Assertions.assertNull(manager.component(ComponentA.class));
		manager.addComponent(new ComponentA());
		Assertions.assertNotNull(manager.component(ComponentA.class));
		ComponentB componentB = manager.removeComponent(new ComponentB());
		Assertions.assertNull(componentB);
	}

	@Test
	void testSubComponents() {
		manager.addComponent(new ComponentC());
		Assertions.assertNotNull(manager.component(ComponentC.class));
		manager.component(ComponentC.class).test();
		manager.removeComponent(manager.component(ComponentC.class));
		Assertions.assertNull(manager.component(ComponentC.class));
		Assertions.assertNull(manager.component(ComponentA.class));
		Assertions.assertNull(manager.component(ComponentB.class));
	}

	@Test
	void testUtilityInstantiation() {
		TestUtils.testUtilityInstantiation(Components.class);
	}

	public static class ComponentA extends Component {
		public ComponentA() {

		}
	}

	public static class ComponentB extends Component {
		public ComponentB() {

		}
	}

	public static class ComponentC extends Component {
		public ComponentC() {

		}

		@Override
		protected void onAdded() {
			super.onAdded();
			addComponent(new ComponentA());
			addComponent(new ComponentB());
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
}
