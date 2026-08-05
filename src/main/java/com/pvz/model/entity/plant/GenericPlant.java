package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.strategy.AttackStrategy;
import com.pvz.strategy.SunProductionStrategy;

/**
 * 配置驱动的植物实体：身份与行为均来自配置。
 */
public final class GenericPlant extends Plant {

    public GenericPlant(
            PlantConfig config,
            int row,
            int col,
            AttackStrategy attackStrategy,
            SunProductionStrategy sunProductionStrategy) {
        super(config, row, col, attackStrategy, sunProductionStrategy);
    }
}
