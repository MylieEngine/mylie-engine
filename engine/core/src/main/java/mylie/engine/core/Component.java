package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Component {
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private ComponentManager manager;
	protected Component() {
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

	protected final <T extends Component> void addComponent(T component) {
		manager.addComponent(component);
	}

	protected final <T extends Component> void removeComponent(T component) {
		manager.removeComponent(component);
	}
}
