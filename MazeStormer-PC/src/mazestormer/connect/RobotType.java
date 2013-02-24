package mazestormer.connect;

public enum RobotType {
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