package mazestormer.remote;

import java.io.File;

import lejos.nxt.Sound;
import mazestormer.command.Command;
import mazestormer.command.SoundPlayCommand;
import mazestormer.robot.RobotSound;
import mazestormer.robot.SoundPlayer;

public class PhysicalSoundPlayer extends NXTComponent implements SoundPlayer,
		MessageListener<Command> {

	public PhysicalSoundPlayer(NXTCommunicator communicator) {
		super(communicator);
		addMessageListener(this);
	}

	@Override
	public void playSound() {
		playSound(RobotSound.MAIN);
	}

	@Override
	public void playSound(RobotSound sound) {
		if (sound != null && RobotSound.isEnabled()) {
			Sound.playSample(new File(sound.getFileName()), Sound.getVolume());
		}
	}

	/**
	 * Handles sound play commands.
	 */

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof SoundPlayCommand))
			return;

		RobotSound sound = ((SoundPlayCommand) command).getSound();
		playSound(sound);
	}

}
