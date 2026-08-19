package com.bxh.pvz.factory;

import com.bxh.pvz.config.ZombieCatalog;
import com.bxh.pvz.config.ZombieConfig;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.config.ZombieType;

/**
 * 僵尸工厂（工厂模式）：根据类型查询配置，再按移动行为键装配策略。
 */
public final class ZombieFactory {

    private final ZombieCatalog catalog;

    public ZombieFactory(ZombieCatalog catalog) {
        this.catalog = catalog;
    }

    public Zombie create(ZombieType type, int row) {
        ZombieConfig config = catalog.of(type);
        return new Zombie(config, row, BehaviorCatalog.moveFor(config.moveBehavior()));
    }
}
