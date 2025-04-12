package mylie.engine.core;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.ClassUtils;

@Slf4j
public class ComponentManager {
	private final List<Component> components;

	public ComponentManager() {
		components = new LinkedList<>();
	}

	public <T extends Component> T addComponent(Class<T> component) {
		T instance = ClassUtils.newInstance(component, this);
		components.add(instance);
		instance.onAdded();
		return instance;
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
