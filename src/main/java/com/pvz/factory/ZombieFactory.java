package com.pvz.factory;

import com.pvz.config.ZombieCatalog;
import com.pvz.config.ZombieConfig;
import com.pvz.model.entity.zombie.BasicZombie;
import com.pvz.model.entity.zombie.ConeheadZombie;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.entity.zombie.ZombieType;
import com.pvz.strategy.MoveLeftStrategy;
import com.pvz.strategy.MoveStrategy;

/**
 * 僵尸工厂（Factory Pattern）：根据类型创建僵尸。
 */
public final class ZombieFactory {

    private final MoveStrategy moveStrategy;

    public ZombieFactory() {
        this(new MoveLeftStrategy());
    }

    public ZombieFactory(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }

    public Zombie create(ZombieType type, int row) {
        ZombieConfig config = ZombieCatalog.of(type);
        return switch (type) {
            case BASIC -> new BasicZombie(config, row, moveStrategy);
            case CONEHEAD -> new ConeheadZombie(config, row, moveStrategy);
        };
    }
}
