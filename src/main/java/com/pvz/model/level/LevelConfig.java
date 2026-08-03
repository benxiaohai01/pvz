package com.pvz.model.level;

import com.pvz.model.entity.plant.PlantType;

import java.util.List;

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
        availablePlants = List.copyOf(availablePlants);
        waves = List.copyOf(waves);
    }
}
