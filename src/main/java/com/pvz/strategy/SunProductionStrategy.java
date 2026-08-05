package com.pvz.strategy;

import com.pvz.model.entity.plant.Plant;
import com.pvz.model.world.GameWorld;

/**
 * 阳光生产策略（Strategy Pattern）：植物如何产生阳光。
 */
public interface SunProductionStrategy {

    void update(Plant plant, GameWorld world, double delta);
}
