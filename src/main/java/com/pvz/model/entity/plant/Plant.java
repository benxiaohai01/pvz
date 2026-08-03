package com.pvz.model.entity.plant;

import com.pvz.config.PlantConfig;
import com.pvz.model.entity.GameObject;

/**
 * 植物抽象基类：生命值 + 位置 + 行为。
 */
public abstract non-sealed class Plant extends GameObject {

    private final PlantConfig config;
    private final int row;
    private final int col;
    private double hp;

    protected Plant(PlantConfig config, int row, int col) {
        super(0, 0);
        this.config = config;
        this.row = row;
        this.col = col;
        this.hp = config.maxHp();
    }

    public final int row() {
        return row;
    }

    public final int col() {
        return col;
    }

    public final PlantConfig config() {
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

    public final int cost() {
        return config.cost();
    }

    /** 由世界在放置时调用，把网格坐标换算为世界坐标。 */
    public final void setCellPosition(double x, double y) {
        setPosition(x, y);
    }
}
