package com.pvz.config;

import com.pvz.model.level.LevelConfig;
import com.pvz.model.level.ZombieWave;

import java.util.List;

import static com.pvz.model.entity.plant.PlantType.PEASHOOTER;
import static com.pvz.model.entity.plant.PlantType.SUNFLOWER;
import static com.pvz.model.entity.plant.PlantType.WALLNUT;
import static com.pvz.model.entity.zombie.ZombieType.BASIC;

/**
 * 关卡配置注册表：所有关卡都是数据，新增关卡只需添加一条记录。
 */
public final class LevelCatalog {

    private LevelCatalog() {
    }

    public static final LevelConfig LEVEL_1_1 = new LevelConfig(
            "1-1", "关卡 1-1", 150,
            List.of(SUNFLOWER, PEASHOOTER),
            List.of(
                    new ZombieWave(10, BASIC, 3, 4),
                    new ZombieWave(20, BASIC, 5, 3),
                    new ZombieWave(30, BASIC, 8, 2.5)));

    public static final LevelConfig LEVEL_1_2 = new LevelConfig(
            "1-2", "关卡 1-2", 150,
            List.of(SUNFLOWER, PEASHOOTER, WALLNUT),
            List.of(
                    new ZombieWave(8, BASIC, 5, 3.5),
                    new ZombieWave(25, BASIC, 8, 2.5),
                    new ZombieWave(45, BASIC, 12, 2)));

    public static final LevelConfig LEVEL_1_3 = new LevelConfig(
            "1-3", "关卡 1-3", 175,
            List.of(SUNFLOWER, PEASHOOTER, WALLNUT),
            List.of(
                    new ZombieWave(8, BASIC, 8, 3),
                    new ZombieWave(28, BASIC, 12, 2.2),
                    new ZombieWave(50, BASIC, 16, 1.8)));

    public static final List<LevelConfig> LEVELS = List.of(LEVEL_1_1, LEVEL_1_2, LEVEL_1_3);
}
