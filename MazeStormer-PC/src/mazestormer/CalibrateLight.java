package mazestormer;

import java.util.Scanner;

import lejos.nxt.*;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.world.ModelType;

public class CalibrateLight {

	@SuppressWarnings("resource")
	public static void main(String[] aArgs) throws Exception {

		Connector connector = new ConnectionProvider()
				.getConnector(ModelType.PHYSICAL);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		LightSensor light = new LightSensor(SensorPort.S1);

		light.setFloodlight(true);

		Scanner scan = new Scanner(System.in);

		while (true) {
			System.out.println(light.readNormalizedValue());
			scan.nextLine();

		}

	}

}
