package mazestormer.connect;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.world.World;

public class ConnectionContext {

	private String deviceName;
	private World world;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = checkNotNull(deviceName);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = checkNotNull(world);
	}

}
