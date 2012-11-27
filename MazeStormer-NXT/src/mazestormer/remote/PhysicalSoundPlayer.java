package mazestormer.remote;

import java.io.File;

import lejos.nxt.Sound;
import mazestormer.command.Command;
import mazestormer.command.PlaySoundCommand;
import mazestormer.robot.RobotSound;
import mazestormer.robot.SoundPlayer;

public class PhysicalSoundPlayer implements SoundPlayer,
		MessageListener<Command> {

	public PhysicalSoundPlayer(NXTCommunicator communicator) {
		communicator.addListener(this);
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
		// TODO Sound commands not working for now
		if (command instanceof PlaySoundCommand) {
			onSoundPlayCommand((PlaySoundCommand) command);
		}
	}

	private void onSoundPlayCommand(PlaySoundCommand command) {
		RobotSound sound = command.getSound();
		playSound(sound);
	}

}
