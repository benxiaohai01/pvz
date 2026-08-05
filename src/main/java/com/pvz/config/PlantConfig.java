package com.pvz.config;

import com.pvz.model.entity.plant.PlantType;

import java.util.Objects;

/**
 * 植物静态配置（Record 值对象）。
 */
public record PlantConfig(
        PlantType type,
        String displayName,
        int cost,
        double cooldown,
        int maxHp,
        double attackInterval,
        int damage,
        double projectileSpeed,
        double sunInterval,
        int sunAmount,
        ColorValue color) {

    public PlantConfig {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(color, "color");
        if (cost < 0) {
            throw new IllegalArgumentException("cost 不能为负数: " + cost);
        }
        if (cooldown < 0) {
            throw new IllegalArgumentException("cooldown 不能为负数: " + cooldown);
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp 必须大于 0: " + maxHp);
        }
        if (attackInterval < 0) {
            throw new IllegalArgumentException("attackInterval 不能为负数: " + attackInterval);
        }
        if (damage < 0) {
            throw new IllegalArgumentException("damage 不能为负数: " + damage);
        }
        if (projectileSpeed < 0) {
            throw new IllegalArgumentException("projectileSpeed 不能为负数: " + projectileSpeed);
        }
        if (sunInterval < 0) {
            throw new IllegalArgumentException("sunInterval 不能为负数: " + sunInterval);
        }
        if (sunAmount < 0) {
            throw new IllegalArgumentException("sunAmount 不能为负数: " + sunAmount);
        }
    }
}
