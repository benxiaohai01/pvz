package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 阳光生产策略（策略模式）：植物如何产生阳光。
 */
public interface SunProductionStrategy {

    void update(Plant plant, GameWorld world, double delta);
}
