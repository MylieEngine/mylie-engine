package mylie.engine.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mylie.engine.core.ComponentManager;
import mylie.engine.core.async.Scheduler;
import mylie.engine.core.async.Target;
import org.junit.jupiter.api.Test;

class EventManagerTest {

	@Test
	void testFireEventWithSingleListener() {
		ComponentManager dummyComponentManager = new ComponentManager();
		EventManager eventManager = new EventManager(dummyComponentManager);
		TheTestListener listener = new TheTestListener();
		TheTestEvent event = new TheTestEvent();
		eventManager.registerListener(listener);
		eventManager.fireEvent(event);
		assertEquals(1, listener.receivedEvents.size());
		assertEquals(event, listener.receivedEvents.getFirst());
	}

	@Test
	void testFireEventWithMultipleListeners() {
		ComponentManager dummyComponentManager = new ComponentManager();
		EventManager eventManager = new EventManager(dummyComponentManager);
		TheTestListener listener1 = new TheTestListener();
		TheTestListener listener2 = new TheTestListener();
		TheTestEvent event = new TheTestEvent();
		eventManager.registerListener(listener1);
		eventManager.registerListener(listener2);
		eventManager.fireEvent(event);
		assertEquals(1, listener1.receivedEvents.size());
		assertEquals(event, listener1.receivedEvents.getFirst());
		assertEquals(1, listener2.receivedEvents.size());
		assertEquals(event, listener2.receivedEvents.getFirst());
	}

	@Test
	void testFireEventWithNoListeners() {
		ComponentManager dummyComponentManager = new ComponentManager();
		EventManager eventManager = new EventManager(dummyComponentManager);
		TheTestEvent event = new TheTestEvent();
		eventManager.fireEvent(event);
	}

	@Test
	void testFireEventAfterUnregisteringListener() {
		ComponentManager dummyComponentManager = new ComponentManager();
		EventManager eventManager = new EventManager(dummyComponentManager);
		TheTestListener listener = new TheTestListener();
		TheTestEvent event = new TheTestEvent();
		eventManager.registerListener(listener);
		eventManager.unregisterListener(listener);
		eventManager.fireEvent(event);
		assertEquals(0, listener.receivedEvents.size());
	}

	@Test
	void testFireEventOnTarget() {
		ComponentManager componentManager = new ComponentManager();
		Queue<Runnable> runnables = new LinkedList<>();
		Target target = new Target("TestTarget", true, false);
		Scheduler scheduler = componentManager.addComponent(Scheduler.class);
		EventManager eventManager = componentManager.addComponent(EventManager.class);
		scheduler.register(target, runnables::add);
		TheTestListener listener = new TheTestListener();
		eventManager.registerListener(listener, target);
		assertEquals(listener.hashCode(), eventManager.listeners().getFirst().hashCode());
		eventManager.fireEvent(new TheTestEvent());
		assertEquals(1, runnables.size());
		eventManager.unregisterListener(listener);
		eventManager.fireEvent(new TheTestEvent());
		assertEquals(1, runnables.size());
		scheduler.unregister(target);
	}

	private static class TheTestListener implements EventListener {
		List<Event> receivedEvents = new ArrayList<>();

		@Override
		public void onEvent(Event event) {
			receivedEvents.add(event);
		}
	}

	private static class TheTestEvent implements Event {
	}
}
