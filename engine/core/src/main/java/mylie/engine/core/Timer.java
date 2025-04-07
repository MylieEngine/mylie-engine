package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;

public class Timer extends Component {
	@Getter(AccessLevel.PUBLIC)
	private volatile Time currentTime;
	private float simulationSpeed = 1.0f;
	private long simulationTime;
	protected Timer(ComponentManager manager) {
		super(manager);
		simulationTime = System.nanoTime();
		currentTime = new Time(0, 0, 0, System.nanoTime(), simulationTime);
	}
}
