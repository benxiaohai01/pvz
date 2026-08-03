package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.model.world.GameWorld;

/**
 * 墙果：高生命值、无主动行为，用来阻挡僵尸。
 */
public final class WallNut extends Plant {

    public WallNut(PlantConfig config, int row, int col) {
        super(config, row, col);
    }

    @Override
    public void update(GameWorld world, double delta) {
        // 无主动行为：靠生命值与碰撞承受攻击
    }
}
