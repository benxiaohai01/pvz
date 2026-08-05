package com.pvz.factory;

import com.pvz.config.PlantCatalog;
import com.pvz.config.PlantConfig;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.entity.plant.PeaShooter;
import com.pvz.model.entity.plant.SunFlower;
import com.pvz.model.entity.plant.WallNut;
import com.pvz.strategy.NoAttackStrategy;
import com.pvz.strategy.NoSunProductionStrategy;
import com.pvz.strategy.PeaAttackStrategy;
import com.pvz.strategy.ProduceSunStrategy;
import com.pvz.strategy.SameRowTargetStrategy;

/**
 * 植物工厂（Factory Pattern）：根据类型创建植物并装配策略。
 */
public final class PlantFactory {

    public Plant create(PlantType type, int row, int col) {
        PlantConfig config = PlantCatalog.of(type);
        return switch (type) {
            case SUNFLOWER -> new SunFlower(
                    config, row, col,
                    new NoAttackStrategy(),
                    new ProduceSunStrategy(config));
            case PEASHOOTER -> new PeaShooter(
                    config, row, col,
                    new PeaAttackStrategy(new SameRowTargetStrategy(), config),
                    new NoSunProductionStrategy());
            case WALLNUT -> new WallNut(
                    config, row, col,
                    new NoAttackStrategy(),
                    new NoSunProductionStrategy());
        };
    }
}
