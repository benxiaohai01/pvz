package com.pvz.config;

import com.pvz.model.entity.zombie.ZombieType;

import java.util.Map;

/**
 * 僵尸配置注册表。
 */
public final class ZombieCatalog {

    private ZombieCatalog() {
    }

    public static final ZombieConfig BASIC = new ZombieConfig(
            ZombieType.BASIC, "普通僵尸", 100, 20, 10, 1, ColorValue.of("#9E9E9E"));

    private static final Map<ZombieType, ZombieConfig> BY_TYPE = Map.of(
            ZombieType.BASIC, BASIC);

    public static ZombieConfig of(ZombieType type) {
        ZombieConfig config = BY_TYPE.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知僵尸类型: " + type);
        }
        return config;
    }
}
