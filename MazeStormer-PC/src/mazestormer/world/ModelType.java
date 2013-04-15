package mazestormer.world;

import peno.htttp.PlayerType;

public enum ModelType {
	PHYSICAL {
		@Override
		public String toString() {
			return "Physical";
		}
	},
	VIRTUAL {
		@Override
		public String toString() {
			return "Virtual";
		}
	};
	
	public static ModelType toModelType(PlayerType pt) {
		switch (pt) {
			case PHYSICAL:
				return PHYSICAL;
			case VIRTUAL:
				return VIRTUAL;
			default:
				return null;
		}
	}
}