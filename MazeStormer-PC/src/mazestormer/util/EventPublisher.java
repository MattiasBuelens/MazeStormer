package mazestormer.util;

import com.google.common.eventbus.EventBus;

public interface EventPublisher {

	/**
	 * Get the event bus on which this publisher posts its events.
	 */
	public EventBus getEventBus();

	/**
	 * Sets the event bus on which to publish events and to listen for events.
	 * 
	 * @param eventBus
	 *            The event bus.
	 */
	public void registerEventBus(EventBus eventBus);

}
