package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 不产阳光策略：用于绝大多数植物。
 */
public final class NoSunProductionStrategy implements SunProductionStrategy {

    @Override
    public void update(Plant plant, GameWorld world, double delta) {
        // 无阳光生产行为
    }
}
