package mazestormer.util;

import com.google.common.eventbus.EventBus;

public abstract class AbstractEventSource implements EventSource {

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

	protected void postEvent(Object event) {
		if (getEventBus() != null)
			getEventBus().post(event);
	}

}
