package mazestormer.util;

import com.google.common.eventbus.EventBus;

public interface EventPublisher {

	/**
	 * Sets the event bus on which all events should be published.
	 * 
	 * The publisher may optionally register itself as listener on the given
	 * event bus if it should also consume events from other sources.
	 * 
	 * @param eventBus
	 *            The event bus on which to publish.
	 */
	public void setEventBus(EventBus eventBus);

}
