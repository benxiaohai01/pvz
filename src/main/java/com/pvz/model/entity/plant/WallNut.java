package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.strategy.AttackStrategy;
import com.pvz.strategy.SunProductionStrategy;

/**
 * 墙果：高生命值、无主动行为，用空策略完成装配。
 */
public final class WallNut extends Plant {

    public WallNut(
            PlantConfig config,
            int row,
            int col,
            AttackStrategy attackStrategy,
            SunProductionStrategy sunProductionStrategy) {
        super(config, row, col, attackStrategy, sunProductionStrategy);
    }
}
