package mazestormer.simulator;

import mazestormer.robot.SoundPlayer;

public class VirtualSoundPlayer implements SoundPlayer{
	
	private static final VirtualSoundPlayer instance = new VirtualSoundPlayer();
	
	private VirtualSoundPlayer(){
		
	}
	
	public static VirtualSoundPlayer getInstance(){
		return instance;
	}

	@Override
	public void playSound(){
		playSound(RoboSound.MAIN);
	}

	@Override
	public void playSound(RoboSound sound){
		if(sound != null && RoboSound.isEnabled()){
			String s = "src/res/NXT sound/wav/";
			s=s+sound.getFile().getName();
			java.io.File file = new java.io.File(s);
			try{
                java.applet.Applet.newAudioClip(file.toURI().toURL()).play();
			}
			catch(java.net.MalformedURLException e){
                e.printStackTrace();
			}
		}
	}
}