package mylie.engine.core;

import java.io.Serial;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Timer extends Component {
	public static final double NANOS_TO_SECONDS = 1_000_000_000.0;

	@Getter(AccessLevel.PUBLIC)
	private Time currentTime;
	private long lastUpdate;
	@Getter
	private Settings settings;

	public Timer() {
		super();

	}

	@Override
	protected void onAdded() {
		super.onAdded();
		Vault component = component(Vault.class);
		EngineSettings engineSettings = component == null ? null : component.item(EngineSettings.class);
		if (engineSettings != null) {
			if (engineSettings.timerSettings() != null) {
				settings = engineSettings.timerSettings();
			} else {
				settings = new Settings();
				engineSettings.timerSettings(settings);
			}
		} else {
			settings = new Settings();
		}

		lastUpdate = System.nanoTime();
		currentTime = new Time(0, 0, 0, 0, 0);
	}

	protected void onUpdate() {
		long now = System.nanoTime();
		long elapsed = now - lastUpdate;
		long newFrameId = currentTime().frameId();
		long newTimeNanosSimulation = (long) (currentTime.timeNanosSimulation()
				+ (elapsed * settings.simulationSpeed()));
		newFrameId++;
		double newDelta = elapsed / NANOS_TO_SECONDS;
		currentTime = new Time(newFrameId, newDelta, newDelta * settings.simulationSpeed(), now,
				newTimeNanosSimulation);
		lastUpdate = now;
	}

	@Setter
	@Getter(AccessLevel.PROTECTED)
	public static class Settings implements Serializable {
		@Serial
		private static final long serialVersionUID = 7692428273946684281L;
		private float simulationSpeed;

		public Settings() {
			this.simulationSpeed = 1.0f;
		}
	}

}
