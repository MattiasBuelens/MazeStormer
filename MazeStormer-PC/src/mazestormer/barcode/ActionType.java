package mazestormer.barcode;

public enum ActionType {

	/**
	 * Play a sound.
	 */
	PLAY_SOUND("Play sound") {
		@Override
		public IAction build() {
			return new SoundAction();
		}
	},
	/**
	 * Rotate 360 degrees clockwise.
	 */
	ROTATE_360_CW("Rotate 360° CW") {
		@Override
		public IAction build() {
			return new RotateClockwiseAction();
		}
	},

	/**
	 * Rotate 360 degrees counter-clockwise.
	 */
	ROTATE_360_CCW("Rotate 360° CCW") {
		@Override
		public IAction build() {
			return new RotateCounterClockwiseAction();
		}
	},

	/**
	 * Travel at high speed.
	 */
	HIGH_SPEED("Travel at high speed") {
		@Override
		public IAction build() {
			return new HighSpeedAction();
		}
	},

	/**
	 * Travel at low speed.
	 */
	LOW_SPEED("Travel at low speed") {
		@Override
		public IAction build() {
			return new LowSpeedAction();
		}
	},

	/**
	 * Wait for 5 seconds.
	 */
	WAIT("Wait for 5 seconds") {
		@Override
		public IAction build() {
			return new WaitAction();
		}
	};

	private final String description;

	private ActionType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public abstract IAction build();

}
