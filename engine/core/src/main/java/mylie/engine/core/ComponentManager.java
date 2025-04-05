package mylie.engine.core;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ComponentManager {
	private final List<Component> components;

	public ComponentManager() {
		components = new LinkedList<>();
	}

	public <T extends Component> void addComponent(Class<T> component) {
		throw new UnsupportedOperationException("Not yet implemented");
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

	public <T extends Component> void removeComponent(T component) {
		boolean removed = components.remove(component);
		if (!removed) {
			log.warn("Component < {} > not found", component.getClass().getSimpleName());
		}
	}
}
