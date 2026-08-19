package com.bxh.pvz.factory;

import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.config.PlantType;

/**
 * 植物工厂（工厂模式）：根据类型查询配置，再按行为键装配策略。
 */
public final class PlantFactory {

    private final PlantCatalog catalog;

    public PlantFactory(PlantCatalog catalog) {
        this.catalog = catalog;
    }

    public Plant create(PlantType type, int row, int col) {
        PlantConfig config = catalog.of(type);
        return new Plant(
                config, row, col,
                BehaviorCatalog.attackFor(config.attackBehavior(), config),
                BehaviorCatalog.sunProductionFor(config.sunBehavior(), config));
    }
}
