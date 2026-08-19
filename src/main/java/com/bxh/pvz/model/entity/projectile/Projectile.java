package com.bxh.pvz.model.entity.projectile;

import com.bxh.pvz.model.entity.GameObject;

/**
 * 弹道基类（密封类）。
 */
public abstract sealed class Projectile extends GameObject permits PeaProjectile {

    private final int damage;

    protected Projectile(double x, double y, int damage) {
        super(x, y);
        this.damage = damage;
    }

    public final int damage() {
        return damage;
    }
}
