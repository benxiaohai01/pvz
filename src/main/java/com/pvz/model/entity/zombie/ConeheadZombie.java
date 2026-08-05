package com.pvz.model.entity.zombie;

import com.pvz.config.ZombieConfig;
import com.pvz.strategy.MoveStrategy;

/**
 * 路障僵尸：更高生命值，其余行为与普通僵尸一致，由配置区分。
 */
public final class ConeheadZombie extends Zombie {

    public ConeheadZombie(ZombieConfig config, int row, MoveStrategy moveStrategy) {
        super(config, row, moveStrategy);
    }
}
