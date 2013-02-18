package mazestormer.robot;

import mazestormer.state.State;

public enum NavigatorState implements State<Navigator2, NavigatorState> {

	// Interruptible
	ROTATE_TO {
		@Override
		public void execute(Navigator2 navigator) {
			navigator.rotateToNode();
		}
	},

	// Interruptible
	TRAVEL {
		@Override
		public void execute(Navigator2 navigator) {
			navigator.travel();
		}
	},

	// Interruptible
	ROTATE_ON {
		@Override
		public void execute(Navigator2 navigator) {
			navigator.rotateOnTarget();
		}
	},

	// Not interruptible
	REPORT {
		@Override
		public void execute(Navigator2 navigator) {
			navigator.report();
		}
	},

	// Not interruptible
	NEXT {
		@Override
		public void execute(Navigator2 navigator) {
			navigator.next();
		}
	};

}