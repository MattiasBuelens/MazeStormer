package mazestormer.physical;

import mazestormer.command.CommandType;
import mazestormer.command.PlaySoundCommand;
import mazestormer.robot.RobotSound;
import mazestormer.robot.SoundPlayer;

public class PhysicalSoundPlayer extends PhysicalComponent implements SoundPlayer {

	public PhysicalSoundPlayer(PhysicalCommunicator communicator) {
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
