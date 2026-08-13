package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;

import java.util.Comparator;
import java.util.Optional;

/**
 * 同行索敌策略：选择同一行、植物前方（右侧）最近的僵尸。
 */
public final class SameRowTargetStrategy implements TargetStrategy<Zombie> {

    @Override
    public Optional<Zombie> findTarget(Plant source, GameWorld world) {
        return world.zombies().stream()
                .filter(z -> !z.isRemoved())
                .filter(z -> z.row() == source.row())
                .filter(z -> z.x() > source.x())
                .min(Comparator.comparingDouble(Zombie::x));
    }
}
