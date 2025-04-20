package mylie.engine.input;

import java.util.function.Consumer;

/**
 * The InputProcessor interface defines a mechanism for processing input events as part
 * of an input handling system. It provides a method to process input events, potentially
 * modify them, or generate additional events during the processing. This makes it suitable
 * for implementing custom event handling or transformation in an input pipeline.
 */
public interface InputProcessor {

	/**
	 * Processes the given input event and potentially modifies or generates additional
	 * input events using the provided {@code additionalEvents} consumer. This method
	 * allows for event handling or transformation as part of an input processing pipeline.
	 *
	 * @param event               the input event to process, which contains the input device,
	 *                            input identifier, and value associated with the event
	 * @param additionalEvents    a consumer for handling additional input events that
	 *                            might be generated during the processing of the given event
	 * @return the result of processing the input event, which could be the same event,
	 *         or a completely new event
	 */
	<D extends InputDevice<D>, I extends Input<D, V>, V> InputEvent<?, ?, ?> process(InputEvent<?, ?, ?> event,
			Consumer<InputEvent<?, ?, ?>> additionalEvents);
}
