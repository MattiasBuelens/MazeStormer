package mazestormer.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumMap;
import java.util.Map;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.condition.ConditionType;
import mazestormer.robot.Robot;

public class VirtualConditionResolvers {

	private Map<ConditionType, VirtualConditionResolver<?, ?>> resolvers = new EnumMap<>(ConditionType.class);

	public VirtualConditionResolvers(Robot robot) {
		// Light value comparing
		LightCompareConditionResolver lightResolver = new LightCompareConditionResolver(robot);
		resolvers.put(ConditionType.LIGHT_GREATER_THAN, lightResolver);
		resolvers.put(ConditionType.LIGHT_SMALLER_THAN, lightResolver);
	}

	public <C extends Condition> ConditionFuture resolve(C condition) {
		checkNotNull(condition);

		@SuppressWarnings("unchecked")
		VirtualConditionResolver<? super C, ?> resolver = (VirtualConditionResolver<? super C, ?>) resolvers
				.get(condition.getType());
		checkNotNull(resolver);

		return resolver.add(condition);
	}

}
