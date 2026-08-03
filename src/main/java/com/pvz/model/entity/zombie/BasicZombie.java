package com.pvz.model.entity.zombie;

import com.pvz.config.ZombieConfig;
import com.pvz.strategy.MoveLeftStrategy;

/**
 * 普通僵尸：hp=100、speed=20、damage=10。
 */
public final class BasicZombie extends Zombie {

    public BasicZombie(ZombieConfig config, int row) {
        super(config, row, new MoveLeftStrategy());
    }
}
