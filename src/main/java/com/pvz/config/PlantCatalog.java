package com.pvz.config;

import com.pvz.model.entity.plant.PlantType;

import java.util.Map;

/**
 * 植物配置注册表：数据来自 config/plants.json，启动时加载。
 */
public final class PlantCatalog {

    private PlantCatalog() {
    }

    private static final Map<PlantType, PlantConfig> BY_TYPE = ConfigLoader.loadPlants();

    public static PlantConfig of(PlantType type) {
        PlantConfig config = BY_TYPE.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知植物类型: " + type);
        }
        return config;
    }
}
