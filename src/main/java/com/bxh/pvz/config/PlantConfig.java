package com.bxh.pvz.config;


import java.util.Objects;

/**
 * 植物静态配置（记录类值对象）。
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
        ColorValue color,
        AttackBehavior attackBehavior,
        SunProductionBehavior sunBehavior,
        String spriteKey,
        Integer frameCount,
        Double animationFps,
        String cardImage) {

    public PlantConfig {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(displayName, "displayName");
        if (displayName.isBlank()) {
            throw new IllegalArgumentException("displayName 不能为空");
        }
        Objects.requireNonNull(color, "color");
        Objects.requireNonNull(attackBehavior, "attackBehavior");
        Objects.requireNonNull(sunBehavior, "sunBehavior");
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
        if (spriteKey != null) {
            if (spriteKey.isBlank()) {
                throw new IllegalArgumentException("spriteKey 不能为空");
            }
            if (frameCount == null || frameCount <= 0) {
                throw new IllegalArgumentException("配置 spriteKey 时必须提供大于 0 的 frameCount");
            }
            if (animationFps == null || animationFps <= 0) {
                throw new IllegalArgumentException("配置 spriteKey 时必须提供大于 0 的 animationFps");
            }
            if (cardImage == null || cardImage.isBlank()) {
                throw new IllegalArgumentException("配置 spriteKey 时必须提供 cardImage");
            }
        } else if (frameCount != null || animationFps != null || cardImage != null) {
            throw new IllegalArgumentException("未配置 spriteKey 时不能配置其他 sprite 字段");
        }
    }
}
