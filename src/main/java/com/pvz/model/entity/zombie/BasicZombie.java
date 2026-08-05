package com.pvz.model.entity.zombie;

import com.pvz.config.ZombieConfig;
import com.pvz.strategy.MoveStrategy;

/**
 * 普通僵尸：数据来自配置，移动策略由工厂注入。
 */
public final class BasicZombie extends Zombie {

    public BasicZombie(ZombieConfig config, int row, MoveStrategy moveStrategy) {
        super(config, row, moveStrategy);
    }
}
