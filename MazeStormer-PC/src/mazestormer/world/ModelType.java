package mazestormer.world;

import peno.htttp.PlayerType;

public enum ModelType {
	PHYSICAL {
		@Override
		public String toString() {
			return "Physical";
		}

		@Override
		public PlayerType toPlayerType() {
			return PlayerType.PHYSICAL;
		}
	},
	VIRTUAL {
		@Override
		public String toString() {
			return "Virtual";
		}

		@Override
		public PlayerType toPlayerType() {
			return PlayerType.VIRTUAL;
		}
	};

	public abstract PlayerType toPlayerType();

	public static ModelType fromPlayerType(PlayerType pt) {
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