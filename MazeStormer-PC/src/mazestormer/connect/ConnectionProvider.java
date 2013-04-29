package mazestormer.connect;

import java.util.EnumMap;

import mazestormer.world.ModelType;
import static com.google.common.base.Preconditions.*;

public class ConnectionProvider {

	private final EnumMap<ModelType, Connector> connectorMap = new EnumMap<ModelType, Connector>(
			ModelType.class);

	public ConnectionProvider() {
		addConnectors();
	}

	public Connector getConnector(ModelType robotType) {
		return connectorMap.get(robotType);
	}

	public void setConnector(ModelType robotType, Connector connector) {
		checkNotNull(robotType);

		if (connector == null) {
			connectorMap.remove(robotType);
		} else {
			connectorMap.put(robotType, connector);
		}
	}

	protected void addConnectors() {
		setConnector(ModelType.PHYSICAL, new PhysicalConnector());
		setConnector(ModelType.VIRTUAL, new VirtualConnector());
	}

}
