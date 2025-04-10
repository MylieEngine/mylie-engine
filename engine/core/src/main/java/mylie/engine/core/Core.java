package mylie.engine.core;

import lombok.Getter;

final class Core {
	@Getter
	private final ComponentManager componentManager;
	Core(EngineSettings settings) {
		componentManager = new ComponentManager();
		Vault vault = componentManager.addComponent(Vault.class);
		vault.addItem(settings);
		componentManager.addComponent(Timer.class);
	}

	void onInit() {

	}

	void onUpdate() {
		componentManager.component(Timer.class).onUpdate();
	}

	void onDestroy() {

	}
}
