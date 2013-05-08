package mazestormer.simulator;

import mazestormer.condition.LightCompareCondition;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;

public class LightCompareConditionResolver extends VirtualConditionResolver<LightCompareCondition, Integer> {

	private final CalibratedLightSensor light;

	public LightCompareConditionResolver(ControllableRobot robot) {
		this.light = robot.getLightSensor();
	}

	@Override
	protected Integer getValue() {
		return light.getNormalizedLightValue();
	}

	@Override
	public boolean matches(LightCompareCondition condition, Integer normalizedLightValue) {
		int threshold = condition.getThreshold();
		switch (condition.getType()) {
		case LIGHT_GREATER_THAN:
			return normalizedLightValue >= threshold;
		case LIGHT_SMALLER_THAN:
			return normalizedLightValue <= threshold;
		default:
			return false;
		}
	}

}
