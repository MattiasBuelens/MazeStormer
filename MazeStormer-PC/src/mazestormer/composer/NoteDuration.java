package mazestormer.composer;

import be.kuleuven.cs.som.annotate.*;

public enum NoteDuration{
	
	// Quarter note = 60
	D16(250), D8(500), D4(1000), D2(2000), D1(4000);
	
	private final int duration;
	
	private NoteDuration(int duration){
		this.duration = duration;
	}
	
	@Immutable
	public int getDuration(){
		return this.duration;
	}
	
	public static final int QNT = 60;
	
	public int calculateDuration(int qnt){
		double ratio = QNT/qnt;
		return ((Double) (getDuration() * ratio)).intValue();
	}
}
