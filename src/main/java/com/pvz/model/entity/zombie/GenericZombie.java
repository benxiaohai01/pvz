package com.pvz.model.entity.zombie;

import com.pvz.config.ZombieConfig;
import com.pvz.strategy.MoveStrategy;

/**
 * 配置驱动的僵尸实体：能力与移动行为均来自配置。
 */
public final class GenericZombie extends Zombie {

    public GenericZombie(ZombieConfig config, int row, MoveStrategy moveStrategy) {
        super(config, row, moveStrategy);
    }
}
