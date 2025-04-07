package mylie.engine.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.ConstructorNotFoundException;

@Slf4j
public class ComponentManager {
	private final List<Component> components;

	public ComponentManager() {
		components = new LinkedList<>();
	}

	public <T extends Component> void addComponent(Class<T> component) {
		try {
			Constructor<T> declaredConstructor = component.getDeclaredConstructor(ComponentManager.class);
			T instance = declaredConstructor.newInstance(this);
			components.add(instance);
			instance.onAdded();
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException
				| IllegalAccessException e) {
			log.error("Failed to add component < {} >", component.getSimpleName(), e);
			throw new ConstructorNotFoundException(component, ComponentManager.class);
		}
	}

	public <T extends Component> T component(Class<T> type) {
		for (Component component : components) {
			if (type.isAssignableFrom(component.getClass())) {
				return type.cast(component);
			}
		}
		log.warn("No component of type < {} > found", type.getSimpleName());
		return null;
	}

	public <T extends Component> T removeComponent(T component) {
		boolean removed = components.remove(component);
		if (!removed) {
			log.warn("Component < {} > not found", component.getClass().getSimpleName());
			return null;
		}
		component.onRemoved();
		return component;
	}
}
