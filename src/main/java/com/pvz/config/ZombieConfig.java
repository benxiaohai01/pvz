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
        ColorValue color,
        MoveBehavior moveBehavior) {

    public ZombieConfig {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(displayName, "displayName");
        if (displayName.isBlank()) {
            throw new IllegalArgumentException("displayName 不能为空");
        }
        Objects.requireNonNull(color, "color");
        Objects.requireNonNull(moveBehavior, "moveBehavior");
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
