package mazestormer.robot;

import lejos.nxt.Sound;

public class PhysicalSoundPlayer implements SoundPlayer{
	
	private static final PhysicalSoundPlayer instance = new PhysicalSoundPlayer();
	
	private PhysicalSoundPlayer(){
		
	}
	
	public static PhysicalSoundPlayer getInstance(){
		return instance;
	}

	@Override
	public void playSound(){
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound){
		if(sound != null && RoboSound.isEnabled())
			Sound.playSoundFile(sound.getFile().getName(), false);
	}
}
