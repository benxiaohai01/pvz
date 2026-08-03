package com.pvz.config;

import com.pvz.model.entity.zombie.ZombieType;

/**
 * 僵尸静态配置（Record 值对象）。
 */
public record ZombieConfig(
        ZombieType type,
        String displayName,
        int maxHp,
        double speed,
        int damage,
        double biteInterval,
        ColorValue color) {
}
