package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class Timer extends Component {
	public static final double NANOS_TO_SECONDS = 1_000_000_000.0;

	@Getter(AccessLevel.PUBLIC)
	private Time currentTime;
	private long lastUpdate;
	private Settings settings;
	protected Timer(ComponentManager manager) {
		super(manager);
		lastUpdate = System.nanoTime();
		currentTime = new Time(0, 0, 0, 0, 0);
	}

	protected void update(){
		long now = System.nanoTime();
		long elapsed=now-lastUpdate;
		long newFrameId= currentTime().frameId();
		long newTimeNanosSimulation= (long) (currentTime.timeNanosSimulation()+(elapsed* settings.simulationSpeed()));
		newFrameId++;
		double newDelta=elapsed/1000000000.0;
		currentTime=new Time(newFrameId,newDelta,newDelta*settings.simulationSpeed(),now,newTimeNanosSimulation);
		lastUpdate=now;
	}

	@Getter(AccessLevel.PROTECTED)
	public static class Settings implements Serializable {
		@Serial
		private static final long serialVersionUID = 7692428273946684281L;
		private float simulationSpeed = 1.0f;

		public Settings(float simulationSpeed) {
			this.simulationSpeed = simulationSpeed;
		}
	}


}
