package mazestormer.robot;

import java.io.File;

public interface SoundPlayer{

	public void playSound();
	
	public void playSound(RoboSound sound);
	
	public enum RoboSound{
		MAIN(new File("liquido.wav")), SONAR(new File("! Sonar.wav"));
		
		private RoboSound(File soundFile){
			this.soundFile = soundFile;
		}
		
		public File getFile(){
			return this.soundFile;
		}
		
		private final File soundFile;	
		
		public static boolean isEnabled(){
            return enable;
		}
    
		public static void switchEnable(){
            enable = (enable == true) ? false : true;
		}
    
		public static void setEnable(boolean request){
            enable = request;
		}
    
		private static boolean enable = true;
	}
}
