package com.pvz.factory;

import com.pvz.config.PlantCatalog;
import com.pvz.config.PlantConfig;
import com.pvz.model.entity.plant.GenericPlant;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;

/**
 * 植物工厂（Factory Pattern）：根据类型查询配置，再按行为键装配策略。
 */
public final class PlantFactory {

    public Plant create(PlantType type, int row, int col) {
        PlantConfig config = PlantCatalog.of(type);
        return new GenericPlant(
                config, row, col,
                BehaviorCatalog.attackFor(config.attackBehavior(), config),
                BehaviorCatalog.sunProductionFor(config.sunBehavior(), config));
    }
}
