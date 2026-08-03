package com.pvz.strategy;

import com.pvz.model.entity.plant.Plant;
import com.pvz.model.world.GameWorld;

/**
 * 无攻击策略：用于不攻击的植物（向日葵、墙果）。
 */
public final class NoAttackStrategy implements AttackStrategy {

    @Override
    public void update(Plant plant, GameWorld world, double delta) {
        // 无攻击行为
    }
}
