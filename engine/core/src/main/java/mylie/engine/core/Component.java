package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Component {
	private final ComponentManager manager;
	protected Component(ComponentManager manager) {
		this.manager = manager;
	}

	protected void onAdded() {
		log.debug("Component< {} > added", this.getClass().getSimpleName());
	}

	protected void onRemoved() {
		log.debug("Component< {} > removed", this.getClass().getSimpleName());
	}

	protected final <T extends Component> T component(Class<T> type) {
		return manager == null ? null : manager.component(type);
	}

	protected final <T extends Component> void addComponent(Class<T> component) {
		manager.addComponent(component);
	}

	protected final <T extends Component> void removeComponent(T component) {
		manager.removeComponent(component);
	}
}
