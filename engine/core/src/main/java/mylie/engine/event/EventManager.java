package mylie.engine.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.core.Component;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.async.Scheduler;
import mylie.engine.core.async.Target;

public class EventManager extends Component {
	@Getter(AccessLevel.PACKAGE)
	private final List<EventListener> listeners;
	public EventManager(ComponentManager manager) {
		super(manager);
		this.listeners = new CopyOnWriteArrayList<>();
	}

	public void registerListener(EventListener listener) {
		listeners.add(listener);
	}

	public void registerListener(EventListener listener, Target target) {
		listeners.add(new Forwarder(listener, component(Scheduler.class), target));
	}

	public void unregisterListener(EventListener listener) {
		listeners.removeIf(l -> l.equals(listener));
	}

	public void fireEvent(Event event) {
		listeners.forEach(listener -> listener.onEvent(event));
	}

	private static class Forwarder implements EventListener {
		private final EventListener listener;
		private final Scheduler scheduler;
		private final Target target;
		public Forwarder(EventListener listener, Scheduler scheduler, Target target) {
			this.listener = listener;
			this.scheduler = scheduler;
			this.target = target;
		}
		@Override
		public void onEvent(Event event) {
			scheduler.submit(() -> listener.onEvent(event), target);
		}

		@SuppressWarnings("EqualsDoesntCheckParameterClass")
		@Override
		public boolean equals(Object o) {
			return Objects.equals(listener, o);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(listener);
		}
	}
}
