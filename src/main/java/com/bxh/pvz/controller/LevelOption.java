package com.bxh.pvz.controller;

import com.bxh.pvz.config.LevelConfig;

/**
 * 提供给视图层的关卡展示数据。
 */
public record LevelOption(String id, String name) {

    public static LevelOption from(LevelConfig config) {
        return new LevelOption(config.id(), config.name());
    }
}
