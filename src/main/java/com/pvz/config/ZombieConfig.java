package com.pvz.config;

import com.pvz.model.entity.zombie.ZombieType;

import java.util.Objects;

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

    public ZombieConfig {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(color, "color");
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp 必须大于 0: " + maxHp);
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("speed 必须大于 0: " + speed);
        }
        if (damage <= 0) {
            throw new IllegalArgumentException("damage 必须大于 0: " + damage);
        }
        if (biteInterval <= 0) {
            throw new IllegalArgumentException("biteInterval 必须大于 0: " + biteInterval);
        }
    }
}
