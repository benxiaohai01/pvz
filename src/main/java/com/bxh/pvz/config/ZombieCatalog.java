package com.bxh.pvz.config;

import java.util.EnumSet;
import java.util.Map;

/**
 * 僵尸配置注册表：数据来自 config/zombies.json，启动时加载。
 */
public final class ZombieCatalog {

    private final Map<ZombieType, ZombieConfig> byType;

    public ZombieCatalog() {
        this(ConfigLoader.loadZombies());
    }

    ZombieCatalog(Map<ZombieType, ZombieConfig> byType) {
        this.byType = Map.copyOf(byType);
        if (!this.byType.keySet().containsAll(EnumSet.allOf(ZombieType.class))) {
            throw new IllegalStateException("zombies.json 未覆盖所有 ZombieType");
        }
    }

    public ZombieConfig of(ZombieType type) {
        ZombieConfig config = byType.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知僵尸类型: " + type);
        }
        return config;
    }
}
