package mazestormer;

import java.io.File;

import lejos.nxt.Sound;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;

public class SoundPlayer {
	
	public static void main(String [] options) throws Exception {
		Connector connector = new ConnectionProvider().getConnector(RobotType.Physical);
		connector.setDeviceName("brons");
		connector.connect();
		
		File f1 = new File("! Fanfare.wav");
		File f2 = new File("! Sonar.wav");
		File f3 = new File("liquido.wav");
		
		// TODO: uploading
		//FileSystem.upload(myFile);
		Sound.playSoundFile(f3.getName(), false);
	}
}
