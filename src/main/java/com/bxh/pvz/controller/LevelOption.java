package com.bxh.pvz.controller;

import com.bxh.pvz.config.LevelConfig;

/**
 * Presentation-safe level data exposed to views.
 */
public record LevelOption(String id, String name) {

    public static LevelOption from(LevelConfig config) {
        return new LevelOption(config.id(), config.name());
    }
}
