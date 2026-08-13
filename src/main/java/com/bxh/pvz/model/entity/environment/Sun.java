package com.bxh.pvz.model.entity.environment;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.GameObject;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 阳光实体：掉落、停留、点击收集。
 */
public final class Sun extends GameObject {

    private final int value;
    private final double groundY;
    private double groundTimer;
    private boolean grounded;

    public Sun(double x, double y, int value) {
        super(x, y);
        this.value = value;
        this.groundY = y + 80;
    }

    public int value() {
        return value;
    }

    public boolean isGrounded() {
        return grounded;
    }

    @Override
    public void update(GameWorld world, double delta) {
        if (!grounded) {
            setPosition(x(), y() + GameConfig.SUN_FALL_SPEED * delta);
            if (y() >= groundY) {
                grounded = true;
            }
        } else {
            groundTimer += delta;
            if (groundTimer >= GameConfig.SUN_GROUND_TTL) {
                markRemoved();
            }
        }
    }
}
