package com.pvz.factory;

import com.pvz.config.ZombieCatalog;
import com.pvz.config.ZombieConfig;
import com.pvz.model.entity.zombie.GenericZombie;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.entity.zombie.ZombieType;

/**
 * 僵尸工厂（Factory Pattern）：根据类型查询配置，再按移动行为键装配策略。
 */
public final class ZombieFactory {

    public Zombie create(ZombieType type, int row) {
        ZombieConfig config = ZombieCatalog.of(type);
        return new GenericZombie(config, row, BehaviorCatalog.moveFor(config.moveBehavior()));
    }
}
