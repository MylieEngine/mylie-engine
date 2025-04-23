package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.core.async.SchedulingStrategy;

@Setter
@Getter
public class EngineSettings {
	@Setter(AccessLevel.PACKAGE)
	private Application application;
	private Timer.Settings timerSettings;
	private boolean handleRestarts = true;
	private SchedulingStrategy schedulingStrategy;
	EngineSettings() {

	}

}
