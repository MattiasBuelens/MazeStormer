package mazestormer.simulator;

import mazestormer.condition.LightCompareCondition;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Robot;

public class LightCompareConditionResolver extends VirtualConditionResolver<LightCompareCondition, Integer> {

	private final CalibratedLightSensor light;

	public LightCompareConditionResolver(Robot robot) {
		this.light = robot.getLightSensor();
	}

	@Override
	protected Integer getValue() {
		return light.getLightValue();
	}

	@Override
	public boolean matches(LightCompareCondition condition, Integer lightValue) {
		int threshold = condition.getThreshold();
		switch (condition.getType()) {
		case LIGHT_GREATER_THAN:
			return lightValue >= threshold;
		case LIGHT_SMALLER_THAN:
			return lightValue <= threshold;
		default:
			return false;
		}
	}

}
