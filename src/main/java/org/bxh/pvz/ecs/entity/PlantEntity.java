package org.bxh.pvz.ecs.entity;

import org.bxh.pvz.config.PlantConfig;
import org.bxh.pvz.ecs.component.AttackComponent;
import org.bxh.pvz.ecs.component.HealthComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;

/**
 * 【设计模式：工厂方法模式（Factory Method）】
 * 植物实体 —— 通过组合组件构造。工厂方法接收 PlantConfig 驱动创建。
 */
public final class PlantEntity extends Entity {

    private final PlantConfig config;
    private final int gridRow;
    private final int gridCol;
    private double sunTimer;

    private PlantEntity(PlantConfig config, int gridRow, int gridCol) {
        this.config = config;
        this.gridRow = gridRow;
        this.gridCol = gridCol;
        this.sunTimer = config.sunInterval();
    }

    public PlantConfig plantConfig() { return config; }
    public int gridRow() { return gridRow; }
    public int gridCol() { return gridCol; }
    public double sunTimer() { return sunTimer; }
    public void setSunTimer(double t) { this.sunTimer = t; }

    /** 根据 PlantConfig 创建植物 */
    public static PlantEntity create(PlantConfig cfg, int row, int col, double x, double y) {
        PlantEntity entity = new PlantEntity(cfg, row, col);
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(cfg.maxHealth()));
        if (cfg.damage() > 0) {
            entity.addComponent(new AttackComponent(cfg.damage(), cfg.attackRange(), cfg.attackCooldown()));
        }
        entity.addComponent(new RenderComponent(cfg.shapeType(), cfg.width(), cfg.height(), cfg.colorHex()));
        return entity;
    }
}