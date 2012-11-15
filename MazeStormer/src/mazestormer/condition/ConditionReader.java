package mazestormer.condition;

import mazestormer.remote.MessageTypeReader;

public class ConditionReader extends MessageTypeReader<Condition> {

	@Override
	public ConditionType getType(int typeId) {
		return ConditionType.values()[typeId];
	}

}
