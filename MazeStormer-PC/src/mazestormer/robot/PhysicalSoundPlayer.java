package mazestormer.robot;

import lejos.nxt.Sound;

public class PhysicalSoundPlayer implements SoundPlayer {

	@Override
	public void playSound() {
		playSound(RobotSound.MAIN);
	}

	@Override
	public void playSound(RobotSound sound) {
		if (sound != null && RobotSound.isEnabled()) {
			Sound.playSoundFile(sound.getFileName(), false);
		}
	}
}
