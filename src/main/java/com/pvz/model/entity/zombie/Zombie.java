package com.pvz.model.entity.zombie;

import com.pvz.config.GameConfig;
import com.pvz.config.ZombieConfig;
import com.pvz.model.entity.GameObject;
import com.pvz.model.world.GameWorld;
import com.pvz.strategy.MoveStrategy;

/**
 * 僵尸抽象基类：生命值 + 移动策略 + 攻击力。
 */
public abstract non-sealed class Zombie extends GameObject {

    private final ZombieConfig config;
    private final MoveStrategy moveStrategy;
    private final int row;
    private double hp;
    private boolean attacking;
    private double biteTimer;

    protected Zombie(ZombieConfig config, int row, MoveStrategy moveStrategy) {
        super(GameConfig.SPAWN_X, 0);
        this.config = config;
        this.row = row;
        this.moveStrategy = moveStrategy;
        this.hp = config.maxHp();
    }

    public final void placeAtRow(double y) {
        setPosition(x(), y);
    }

    public final int row() {
        return row;
    }

    public final ZombieConfig config() {
        return config;
    }

    public final double hp() {
        return hp;
    }

    public final void takeDamage(double amount) {
        hp = Math.max(0, hp - amount);
    }

    public final boolean isDead() {
        return hp <= 0;
    }

    public final int damage() {
        return config.damage();
    }

    public final double biteInterval() {
        return config.biteInterval();
    }

    public final double speed() {
        return config.speed();
    }

    public final boolean isAttacking() {
        return attacking;
    }

    /** 累计啃咬计时，到间隔返回 true（每次调用推进 delta）。 */
    public final boolean consumeBite(double delta) {
        biteTimer += delta;
        if (biteTimer >= biteInterval()) {
            biteTimer -= biteInterval();
            return true;
        }
        return false;
    }

    public final void resetBiteTimer() {
        biteTimer = 0;
    }

    @Override
    public void update(GameWorld world, double delta) {
        if (moveStrategy.canMove(this, world)) {
            setPosition(x() - speed() * delta, y());
            attacking = false;
        } else {
            attacking = true;
        }
    }
}
