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
		return normalizedLightValue >= condition.getMinThreshold()
				&& normalizedLightValue <= condition.getMaxThreshold();
	}

}
