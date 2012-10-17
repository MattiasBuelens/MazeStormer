package mazestormer.connect;

import java.util.EnumMap;

public class ConnectionProvider {

	private EnumMap<RobotType, Connector> connectorMap = new EnumMap<RobotType, Connector>(
			RobotType.class);

	public ConnectionProvider() {
		addConnectors();
	}

	public Connector getConnector(RobotType robotType) {
		return connectorMap.get(robotType);
	}

	public void setConnector(RobotType robotType, Connector connector) {
		if (robotType == null)
			throw new IllegalArgumentException("Robot type must be effective.");

		if (connector == null)
			connectorMap.remove(robotType);
		else
			connectorMap.put(robotType, connector);
	}

	protected void addConnectors() {
		setConnector(RobotType.Physical, new PhysicalConnector());
		setConnector(RobotType.Virtual, new VirtualConnector());
	}

}
