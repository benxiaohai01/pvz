package com.bxh.pvz.config;

import java.util.List;
import java.util.Objects;

/**
 * 关卡配置（Record 值对象）：草坪/阳光/可用植物/僵尸波次全部数据化。
 */
public record LevelConfig(
        String id,
        String name,
        int initialSun,
        List<PlantType> availablePlants,
        List<ZombieWave> waves) {

    public LevelConfig {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (initialSun < 0) {
            throw new IllegalArgumentException("initialSun 不能为负数: " + initialSun);
        }
        availablePlants = List.copyOf(availablePlants);
        waves = List.copyOf(waves);
        if (availablePlants.isEmpty()) {
            throw new IllegalArgumentException("关卡至少需要一种可选植物");
        }
        if (waves.isEmpty()) {
            throw new IllegalArgumentException("关卡至少需要一个僵尸波次");
        }
    }
}
