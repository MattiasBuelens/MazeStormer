package mazestormer.ui;

import java.io.File;
import java.net.MalformedURLException;

/**
 * The sound manager of the UI.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class UISound{

	/**
	 * Returns if the UI sound is enabled or not.
	 * 
	 * @return	Returns if the UI sound is enabled or not.
	 * 			| result == enable
	 */
	public static boolean isEnabled(){
		return enable;
	}
	
	/**
	 * Switches the state of the UI sound.
	 * 
	 *  @post	The state of the UI sound is inverted.
	 *  		| new.enable != enable
	 */
	public static void switchEnable(){
		enable = (enable == true) ? false : true;
	}
	
	/**
	 * Sets the state of the UI sound to the given request.
	 * 
	 * @param 	request
	 * 			The new state for the sound of the UI.
	 * @post	The state of the UI sound is set to
	 * 			the given request.
	 * 			| new.enable == request
	 */
	public static void setEnable(boolean request){
		enable = request;
	}
	
	/**
	 * Variable containing the state of the UI sound.
	 */
	private static boolean enable = false;
	
	/**
	 * Plays an introduction sound, if the sound of the UI is enabled.
	 */
	public static void playIntroSound(){
		if(isEnabled()){
			File file = new File("src/res/sounds/Electrical Sweep Sweeper.wav");
			try{
				java.applet.Applet.newAudioClip(file.toURI().toURL()).play();
			}
			catch(MalformedURLException e){
				e.printStackTrace();
			}
		}
	}
}
