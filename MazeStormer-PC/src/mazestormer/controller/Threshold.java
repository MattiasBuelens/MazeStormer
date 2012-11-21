package mazestormer.controller;

public enum Threshold {
	BLACK_WHITE(50), WHITE_BLACK(50);
	
	private Threshold(int thresholdValue){
		setThresholdValue(thresholdValue);
	}
	
	private int thresholdValue;
	
	public int getThresholdValue(){
		return this.thresholdValue;
	}
	
	public void setThresholdValue(int request){
		this.thresholdValue = Math.abs(request) % 101;
	}
}
