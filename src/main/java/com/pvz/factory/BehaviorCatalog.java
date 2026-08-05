package com.pvz.factory;

import com.pvz.config.AttackBehavior;
import com.pvz.config.MoveBehavior;
import com.pvz.config.PlantConfig;
import com.pvz.config.SunProductionBehavior;
import com.pvz.strategy.AttackStrategy;
import com.pvz.strategy.MoveLeftStrategy;
import com.pvz.strategy.MoveStrategy;
import com.pvz.strategy.NoAttackStrategy;
import com.pvz.strategy.NoSunProductionStrategy;
import com.pvz.strategy.PeaAttackStrategy;
import com.pvz.strategy.ProduceSunStrategy;
import com.pvz.strategy.SameRowTargetStrategy;
import com.pvz.strategy.SunProductionStrategy;

/**
 * 把配置中的行为键映射为具体策略实例。
 * 增长规模受行为数量约束，而不是实体数量。
 */
public final class BehaviorCatalog {

    private BehaviorCatalog() {
    }

    public static AttackStrategy attackFor(AttackBehavior behavior, PlantConfig config) {
        return switch (behavior) {
            case PEA -> new PeaAttackStrategy(new SameRowTargetStrategy(), config);
            case NONE -> new NoAttackStrategy();
        };
    }

    public static SunProductionStrategy sunProductionFor(SunProductionBehavior behavior, PlantConfig config) {
        return switch (behavior) {
            case PRODUCE_SUN -> new ProduceSunStrategy(config);
            case NONE -> new NoSunProductionStrategy();
        };
    }

    public static MoveStrategy moveFor(MoveBehavior behavior) {
        return switch (behavior) {
            case MOVE_LEFT -> new MoveLeftStrategy();
        };
    }
}
