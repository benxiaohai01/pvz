package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 攻击策略（Strategy Pattern）：植物如何发起攻击。
 */
public interface AttackStrategy {

    void update(Plant plant, GameWorld world, double delta);
}
