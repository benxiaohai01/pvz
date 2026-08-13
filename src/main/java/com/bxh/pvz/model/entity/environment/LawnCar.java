package com.bxh.pvz.model.entity.environment;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.GameObject;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 小推车：每行一辆，僵尸突破防线时触发，消灭该行所有僵尸。
 */
public final class LawnCar extends GameObject {

    private final int row;
    private boolean triggered;
    private boolean moving;

    public LawnCar(int row) {
        super(GameConfig.CAR_X, 0);
        this.row = row;
    }

    public void placeAtRow(double y) {
        setPosition(x(), y);
    }

    public int row() {
        return row;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void trigger() {
        if (!triggered) {
            triggered = true;
            moving = true;
        }
    }

    @Override
    public void update(GameWorld world, double delta) {
        if (moving) {
            setPosition(x() + GameConfig.LAWN_CAR_SPEED * delta, y());
            if (x() > world.lawn().rightX() + GameConfig.LAWN_CAR_OFFSCREEN_MARGIN) {
                markRemoved();
            }
        }
    }
}
