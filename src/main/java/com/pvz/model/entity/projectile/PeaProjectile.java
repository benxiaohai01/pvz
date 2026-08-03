package com.pvz.model.entity.projectile;

import com.pvz.model.world.GameWorld;

/**
 * 豌豆子弹：沿所在行向右飞行。
 */
public final class PeaProjectile extends Projectile {

    private final double speed;
    private final int row;

    public PeaProjectile(double x, double y, int row, int damage, double speed) {
        super(x, y, damage);
        this.row = row;
        this.speed = speed;
    }

    public int row() {
        return row;
    }

    @Override
    public void update(GameWorld world, double delta) {
        setPosition(x() + speed * delta, y());
        if (x() > world.lawn().rightX() + 60) {
            markRemoved();
        }
    }
}
