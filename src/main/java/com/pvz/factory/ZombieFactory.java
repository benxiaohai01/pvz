package com.pvz.factory;

import com.pvz.config.ZombieCatalog;
import com.pvz.config.ZombieConfig;
import com.pvz.model.entity.zombie.BasicZombie;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.entity.zombie.ZombieType;

/**
 * 僵尸工厂（Factory Pattern）：根据类型创建僵尸。
 */
public final class ZombieFactory {

    public Zombie create(ZombieType type, int row) {
        ZombieConfig config = ZombieCatalog.of(type);
        return switch (type) {
            case BASIC -> new BasicZombie(config, row);
        };
    }
}
