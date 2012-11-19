package mazestormer.remote;

import mazestormer.robot.SoundPlayer;

public class RemoteSoundPlayer extends RemoteComponent implements SoundPlayer {

	public RemoteSoundPlayer(RemoteCommunicator communicator) {
		super(communicator);
	}

	@Override
	public void playSound() {
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound) {
		// TODO Implement remote sound playing

	}

}
