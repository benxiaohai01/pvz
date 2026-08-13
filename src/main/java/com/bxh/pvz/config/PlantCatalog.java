package com.bxh.pvz.config;

import java.util.EnumSet;
import java.util.Map;

/**
 * 植物配置注册表：数据来自 config/plants.json，启动时加载。
 */
public final class PlantCatalog {

    private final Map<PlantType, PlantConfig> byType;

    public PlantCatalog() {
        this(ConfigLoader.loadPlants());
    }

    PlantCatalog(Map<PlantType, PlantConfig> byType) {
        this.byType = Map.copyOf(byType);
        if (!this.byType.keySet().containsAll(EnumSet.allOf(PlantType.class))) {
            throw new IllegalStateException("plants.json 未覆盖所有 PlantType");
        }
    }

    public PlantConfig of(PlantType type) {
        PlantConfig config = byType.get(type);
        if (config == null) {
            throw new IllegalArgumentException("未知植物类型: " + type);
        }
        return config;
    }
}
