package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.strategy.AttackStrategy;
import com.pvz.strategy.SunProductionStrategy;

/**
 * 豌豆射手：攻击与产阳光行为均由策略驱动。
 */
public final class PeaShooter extends Plant {

    public PeaShooter(
            PlantConfig config,
            int row,
            int col,
            AttackStrategy attackStrategy,
            SunProductionStrategy sunProductionStrategy) {
        super(config, row, col, attackStrategy, sunProductionStrategy);
    }
}
