package com.pvz.strategy;

import com.pvz.config.GameConfig;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.world.GameWorld;

/**
 * 向左移动策略：前方有植物阻挡时停止。
 */
public final class MoveLeftStrategy implements MoveStrategy {

    @Override
    public boolean canMove(Zombie zombie, GameWorld world) {
        return world.plantsInRow(zombie.row()).stream()
                .noneMatch(p -> !p.isRemoved() && blocks(p, zombie));
    }

    private static boolean blocks(Plant plant, Zombie zombie) {
        double plantRight = plant.x() + GameConfig.PLANT_HALF_WIDTH;
        double zombieFront = zombie.x() - GameConfig.ZOMBIE_HALF_WIDTH;
        return plant.x() <= zombie.x() && plantRight >= zombieFront;
    }
}
