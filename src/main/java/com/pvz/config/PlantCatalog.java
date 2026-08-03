package com.pvz.config;

import com.pvz.model.entity.plant.PlantType;

import java.util.Map;

/**
 * 植物配置注册表：新增植物只需在这里添加一条配置，再由工厂注册创建逻辑。
 */
public final class PlantCatalog {

    private PlantCatalog() {
    }

    public static final PlantConfig SUNFLOWER = new PlantConfig(
            PlantType.SUNFLOWER, "向日葵", 50, 7.5, 100,
            0, 0, 0, 5, 25, ColorValue.of("#FFD700"));

    public static final PlantConfig PEASHOOTER = new PlantConfig(
            PlantType.PEASHOOTER, "豌豆射手", 100, 7.5, 100,
            2, 20, 260, 0, 0, ColorValue.of("#32CD32"));

    public static final PlantConfig WALLNUT = new PlantConfig(
            PlantType.WALLNUT, "墙果", 50, 30, 400,
            0, 0, 0, 0, 0, ColorValue.of("#A0522D"));

    private static final Map<PlantType, PlantConfig> BY_TYPE = Map.of(
            PlantType.SUNFLOWER, SUNFLOWER,
            PlantType.PEASHOOTER, PEASHOOTER,
            PlantType.WALLNUT, WALLNUT);

    public static PlantConfig of(PlantType type) {
        PlantConfig config = BY_TYPE.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知植物类型: " + type);
        }
        return config;
    }
}
