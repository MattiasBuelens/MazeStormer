package mazestormer.ui;

import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;

import mazestormer.util.EventPublisher;

public abstract class ViewPanel extends JPanel implements EventPublisher {

	private static final long serialVersionUID = 1L;

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
