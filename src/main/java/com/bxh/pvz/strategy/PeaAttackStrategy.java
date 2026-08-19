package com.bxh.pvz.strategy;

import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.PeaProjectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 豌豆攻击策略：同一行存在僵尸时，按攻击间隔发射一颗豌豆。
 */
public final class PeaAttackStrategy implements AttackStrategy {

    private final TargetStrategy<Zombie> targetStrategy;
    private final double attackInterval;
    private final int damage;
    private final double projectileSpeed;
    private double timer;

    public PeaAttackStrategy(TargetStrategy<Zombie> targetStrategy, PlantConfig config) {
        this.targetStrategy = targetStrategy;
        this.attackInterval = config.attackInterval();
        this.damage = config.damage();
        this.projectileSpeed = config.projectileSpeed();
        this.timer = attackInterval;
    }

    @Override
    public void update(Plant plant, GameWorld world, double delta) {
        if (targetStrategy.findTarget(plant, world).isPresent()) {
            timer += delta;
            if (timer >= attackInterval) {
                timer = 0;
                world.addProjectile(new PeaProjectile(
                        plant.x() + GameConfig.PLANT_HALF_WIDTH,
                        plant.y(),
                        plant.row(),
                        damage,
                        projectileSpeed));
            }
        } else {
            // 无目标时保持待发状态，目标一出现立即开火
            timer = attackInterval;
        }
    }
}
