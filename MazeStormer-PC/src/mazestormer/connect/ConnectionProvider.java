package mazestormer.connect;

import java.util.EnumMap;
import static com.google.common.base.Preconditions.*;

public class ConnectionProvider {

	private final EnumMap<RobotType, Connector> connectorMap = new EnumMap<RobotType, Connector>(
			RobotType.class);

	public ConnectionProvider() {
		addConnectors();
	}

	public Connector getConnector(RobotType robotType) {
		return connectorMap.get(robotType);
	}

	public void setConnector(RobotType robotType, Connector connector) {
		checkNotNull(robotType);

		if (connector == null) {
			connectorMap.remove(robotType);
		} else {
			connectorMap.put(robotType, connector);
		}
	}

	protected void addConnectors() {
		setConnector(RobotType.Physical, new PhysicalConnector());
		setConnector(RobotType.Virtual, new VirtualConnector());
		setConnector(RobotType.Remote, new RemoteConnector());
	}

}
