package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.world.GameWorld;

/**
 * 向日葵：每隔固定时间生产阳光。
 */
public final class SunFlower extends Plant {

    private double timer;

    public SunFlower(PlantConfig config, int row, int col) {
        super(config, row, col);
    }

    @Override
    public void update(GameWorld world, double delta) {
        timer += delta;
        if (timer >= config().sunInterval()) {
            timer = 0;
            world.addSun(new Sun(x(), y() - 18, config().sunAmount()));
        }
    }
}
