package mazestormer.world;

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
	}
}