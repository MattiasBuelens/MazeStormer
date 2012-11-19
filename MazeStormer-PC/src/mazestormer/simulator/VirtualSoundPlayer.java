package mazestormer.simulator;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import mazestormer.robot.SoundPlayer;

public class VirtualSoundPlayer implements SoundPlayer {

	private static final String soundDirectory = "/res/NXT sound/wav/";

	@Override
	public void playSound() {
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound) {
		if (sound != null && RoboSound.isEnabled()) {
			URL soundUrl = getSoundUrl(sound);
			playAudio(soundUrl);
		}
	}

	public URL getSoundUrl(RoboSound sound) {
		Path path = Paths.get(soundDirectory, sound.getFileName());
		String resPath = path.toString().replace(File.separatorChar, '/');
		return VirtualSoundPlayer.class.getResource(resPath);
	}

	public void playAudio(URL url) {
		AudioInputStream ais = null;
		Clip clip = null;
		try {
			ais = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.addLineListener(new AudioListener(clip));
			clip.open(ais);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class AudioListener implements LineListener {
		private final Clip clip;

		public AudioListener(Clip clip) {
			this.clip = clip;
		}

		@Override
		public synchronized void update(LineEvent event) {
			Type eventType = event.getType();
			if (eventType == Type.STOP || eventType == Type.CLOSE) {
				clip.close();
			}
		}
	}

}