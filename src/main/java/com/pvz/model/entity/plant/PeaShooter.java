package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.strategy.AttackStrategy;
import com.pvz.model.world.GameWorld;

/**
 * 豌豆射手：攻击行为委托给 AttackStrategy。
 */
public final class PeaShooter extends Plant {

    private final AttackStrategy attackStrategy;

    public PeaShooter(PlantConfig config, int row, int col, AttackStrategy attackStrategy) {
        super(config, row, col);
        this.attackStrategy = attackStrategy;
    }

    @Override
    public void update(GameWorld world, double delta) {
        attackStrategy.update(this, world, delta);
    }
}
