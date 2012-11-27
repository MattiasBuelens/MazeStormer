package mazestormer.remote;

import mazestormer.command.CommandType;
import mazestormer.command.PlaySoundCommand;
import mazestormer.robot.RobotSound;
import mazestormer.robot.SoundPlayer;

public class RemoteSoundPlayer extends RemoteComponent implements SoundPlayer {

	public RemoteSoundPlayer(RemoteCommunicator communicator) {
		super(communicator);
	}

	@Override
	public void playSound() {
		playSound(RobotSound.MAIN);
	}

	@Override
	public void playSound(RobotSound sound) {
		send(new PlaySoundCommand(CommandType.PLAY_SOUND, sound));
	}

}
