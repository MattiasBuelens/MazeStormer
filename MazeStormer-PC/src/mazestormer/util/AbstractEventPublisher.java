package mazestormer.util;

import com.google.common.eventbus.EventBus;

public abstract class AbstractEventPublisher implements EventPublisher {

	private EventBus eventBus;

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void registerEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

}
