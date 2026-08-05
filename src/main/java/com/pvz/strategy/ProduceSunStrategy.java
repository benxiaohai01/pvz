package com.pvz.strategy;

import com.pvz.config.GameConfig;
import com.pvz.config.PlantConfig;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.world.GameWorld;

/**
 * 定时产阳光策略：按配置间隔在植物上方生成阳光。
 */
public final class ProduceSunStrategy implements SunProductionStrategy {

    private final double sunInterval;
    private final int sunAmount;
    private double timer;

    public ProduceSunStrategy(PlantConfig config) {
        this.sunInterval = config.sunInterval();
        this.sunAmount = config.sunAmount();
    }

    @Override
    public void update(Plant plant, GameWorld world, double delta) {
        timer += delta;
        if (timer >= sunInterval) {
            timer = 0;
            world.addSun(new Sun(
                    plant.x(),
                    plant.y() + GameConfig.SUN_SPAWN_OFFSET_Y,
                    sunAmount));
        }
    }
}
