package mazestormer.remote;

import java.io.File;

import lejos.nxt.Sound;
import mazestormer.command.Command;
import mazestormer.command.SoundPlayCommand;
import mazestormer.robot.SoundPlayer;

public class PhysicalSoundPlayer extends NXTComponent implements SoundPlayer {

	public PhysicalSoundPlayer(NXTCommunicator communicator) {
		super(communicator);
		setup();
	}

	private void setup() {
		addMessageListener(new SoundPlayCommandListener());
	}

	@Override
	public void playSound() {
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound) {
		if (sound != null && RoboSound.isEnabled()) {
			Sound.playSample(new File(sound.getFileName()), Sound.getVolume());
		}
	}

	/**
	 * Handles sound play commands.
	 */
	private class SoundPlayCommandListener implements MessageListener<Command> {

		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof SoundPlayCommand))
				return;

			RoboSound sound = ((SoundPlayCommand) command).getSound();
			playSound(sound);
		}

	}
}
