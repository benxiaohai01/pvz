package com.bxh.pvz.controller;

import com.bxh.pvz.config.PlantType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks per-plant card cooldown state.
 */
public final class PlantCooldowns {

    private final Map<PlantType, Double> remaining = new EnumMap<>(PlantType.class);

    public double remaining(PlantType type) {
        return remaining.getOrDefault(type, 0.0);
    }

    public void start(PlantType type, double cooldown) {
        remaining.put(type, cooldown);
    }

    public void tick(double delta) {
        remaining.replaceAll((type, value) -> Math.max(0, value - delta));
    }
}
