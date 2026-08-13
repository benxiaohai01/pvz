package com.bxh.pvz.strategy;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

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
