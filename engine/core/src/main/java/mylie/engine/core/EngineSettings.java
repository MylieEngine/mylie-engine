package mylie.engine.core;

import lombok.Getter;
import lombok.Setter;
import mylie.engine.core.async.Scheduler;
import mylie.engine.core.async.SchedulingStrategies;

@Setter
@Getter
public class EngineSettings {

	private Timer.Settings timerSettings;
	private boolean handleRestarts = true;
	private Scheduler.SchedulingStrategy schedulingStrategy;
	EngineSettings() {

	}

}
