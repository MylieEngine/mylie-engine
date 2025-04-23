package mylie.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.async.Result;

@Slf4j
public class ComponentManager {
	private final List<Component> components;
	@Getter(AccessLevel.PACKAGE)
	private boolean running;
	public ComponentManager() {
		components = new CopyOnWriteArrayList<>();
	}

	public <T extends Component> T addComponent(T component) {
		components.add(component);
		component.manager(this);
		component.onAdded();
		return component;
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
		if (running && component instanceof Components.Base base && base.currentlyEnabled()) {
			base.onDisable();
			base.onDestroy();
		}
		component.onRemoved();
		return component;
	}

	public void updateComponents(boolean running) {
		this.running = running;
		List<Result<?>> results = new ArrayList<>();
		for (Component component : components) {
			if (component instanceof Components.Base base) {
				results.add(base.updateTask().execute());
			}
		}
		results.forEach(Result::get);
		if (!running) {
			components.forEach(this::removeComponent);
		}
	}
}
