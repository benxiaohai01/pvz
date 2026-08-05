package com.pvz.config;

import com.pvz.model.entity.zombie.ZombieType;

import java.util.Map;

/**
 * 僵尸配置注册表：数据来自 config/zombies.json，启动时加载。
 */
public final class ZombieCatalog {

    private ZombieCatalog() {
    }

    private static final Map<ZombieType, ZombieConfig> BY_TYPE = ConfigLoader.loadZombies();

    public static ZombieConfig of(ZombieType type) {
        ZombieConfig config = BY_TYPE.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知僵尸类型: " + type);
        }
        return config;
    }
}
