package com.bxh.pvz.controller;

import com.bxh.pvz.config.ColorValue;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;

/**
 * Presentation-safe plant data exposed to views.
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
