package com.bxh.pvz.strategy;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 向左移动策略：前方有植物阻挡时停止。
 */
public final class MoveLeftStrategy implements MoveStrategy {

    @Override
    public boolean canMove(Zombie zombie, GameWorld world) {
        return world.plantsInRow(zombie.row()).stream()
                .noneMatch(plant -> !plant.isRemoved() && blocks(plant, zombie));
    }

    private static boolean blocks(Plant plant, Zombie zombie) {
        double plantRight = plant.x() + GameConfig.PLANT_HALF_WIDTH;
        double zombieFront = zombie.x() - GameConfig.ZOMBIE_HALF_WIDTH;
        return plant.x() <= zombie.x() && plantRight >= zombieFront;
    }
}
