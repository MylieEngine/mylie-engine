package mylie.engine.core;

import java.util.LinkedList;
import java.util.List;

public class ComponentManager {
	private final List<Component> components;

	public ComponentManager() {
		components = new LinkedList<>();
	}

	public <T extends Component> void addComponent(Class<T> component) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public <T extends Component> T component(Class<T> type) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public <T extends Component> void removeComponent(T component) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
