package mazestormer.condition;

import mazestormer.remote.MessageType;

public enum ConditionType implements MessageType<Condition> {

	/*
	 * Light sensor
	 */

	LIGHT_GREATER_THAN {
		@Override
		public Condition build() {
			return new LightCompareCondition(this);
		}
	},

	LIGHT_SMALLER_THAN {
		@Override
		public Condition build() {
			return new LightCompareCondition(this);
		}
	};

}
