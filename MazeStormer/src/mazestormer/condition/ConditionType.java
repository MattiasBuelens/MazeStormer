package mazestormer.condition;

import mazestormer.remote.MessageType;

public enum ConditionType implements MessageType<Condition> {

	/*
	 * Light sensor
	 */

	LIGHT_HIGHER_THAN {
		@Override
		public Condition build() {
			return new LightCompareCondition(this);
		}
	},

	LIGHT_LOWER_THAN {
		@Override
		public Condition build() {
			return new LightCompareCondition(this);
		}
	};

}
