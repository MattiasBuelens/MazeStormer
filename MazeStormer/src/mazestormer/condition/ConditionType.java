package mazestormer.condition;

import mazestormer.remote.MessageType;

public enum ConditionType implements MessageType<Condition> {

	/*
	 * Light sensor
	 */

	LIGHT_HIGHER_THAN {
		@Override
		public Condition build() {
			// TODO Auto-generated method stub
			return null;
		}
	},

	LIGHT_LOWER_THAN {
		@Override
		public Condition build() {
			// TODO Auto-generated method stub
			return null;
		}
	};

}
