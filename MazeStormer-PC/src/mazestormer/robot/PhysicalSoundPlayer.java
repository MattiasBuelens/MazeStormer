package mazestormer.robot;

import lejos.nxt.Sound;

public class PhysicalSoundPlayer implements SoundPlayer {

	@Override
	public void playSound() {
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound) {
		if (sound != null && RoboSound.isEnabled()) {
			Sound.playSoundFile(sound.getFileName(), false);
		}
	}
}
