package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.strategy.AttackStrategy;
import com.pvz.strategy.SunProductionStrategy;

/**
 * 向日葵：产阳光行为委托给 SunProductionStrategy。
 */
public final class SunFlower extends Plant {

    public SunFlower(
            PlantConfig config,
            int row,
            int col,
            AttackStrategy attackStrategy,
            SunProductionStrategy sunProductionStrategy) {
        super(config, row, col, attackStrategy, sunProductionStrategy);
    }
}
