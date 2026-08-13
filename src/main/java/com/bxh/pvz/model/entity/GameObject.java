package com.bxh.pvz.model.entity;

import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;
import com.bxh.pvz.util.Vector2;

/**
 * 游戏对象基类（Sealed Class）：限定继承体系，保证类型安全。
 */
public abstract sealed class GameObject permits Plant, Zombie, Projectile, Sun, LawnCar {

    private double x;
    private double y;
    private boolean removed;

    protected GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public final double x() {
        return x;
    }

    public final double y() {
        return y;
    }

    public final Vector2 position() {
        return new Vector2(x, y);
    }

    public final void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected final void setPosition(Vector2 position) {
        this.x = position.x();
        this.y = position.y();
    }

    public final boolean isRemoved() {
        return removed;
    }

    public final void markRemoved() {
        removed = true;
    }

    /** 撤销操作时恢复对象（例如铲除命令的 undo）。 */
    public final void restore() {
        removed = false;
    }

    /** 每帧的行为更新，由具体对象决定自己的生命周期。 */
    public abstract void update(GameWorld world, double delta);
}
