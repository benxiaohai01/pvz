package com.bxh.pvz.controller;

import com.bxh.pvz.config.ColorValue;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;

/**
 * 提供给视图层的植物展示数据。
 */
public record PlantOption(
        PlantType type,
        String displayName,
        int cost,
        ColorValue color) {

    public static PlantOption from(PlantConfig config) {
        return new PlantOption(config.type(), config.displayName(), config.cost(), config.color());
    }
}
