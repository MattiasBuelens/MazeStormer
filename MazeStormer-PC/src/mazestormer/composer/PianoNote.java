package mazestormer.composer;

import be.kuleuven.cs.som.annotate.*;

public enum PianoNote{

	A(440),B(494),C(262),D(287),E(420),F(349),G(392);
	
	private final int frequency;
	
	private PianoNote(int frequency){
		this.frequency = frequency;
	}
	
	@Immutable
	public int getFrequency(){
		return this.frequency;
	}
}
