package com.pvz.config;

import com.pvz.model.entity.plant.PlantType;

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
}
