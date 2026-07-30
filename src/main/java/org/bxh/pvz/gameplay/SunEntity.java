package org.bxh.pvz.gameplay;

import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;

/**
 * 【设计模式：状态模式（State）—— sunValue 表示产出自向日葵还是天空掉落】
 * 太阳实体 —— 可点击收集的阳光，从向日葵产出或天空掉落。
 * 向日葵产出：sunValue=25；天空掉落：sunValue=50。
 */
public final class SunEntity extends Entity {

    private final int sunValue;

    private SunEntity(int sunValue) { this.sunValue = sunValue; }

    public int sunValue() { return sunValue; }

    /** 向日葵产出的太阳（慢速下落） */
    public static SunEntity fromSunflower(double x, double y) {
        SunEntity sun = new SunEntity(25);
        sun.addComponent(TransformComponent.at(x, y));
        var mv = new MovementComponent(30);
        mv.setVelocity(0, 30);
        sun.addComponent(mv);
        sun.addComponent(new RenderComponent(RenderComponent.ShapeType.CIRCLE, 22, 22, "#FFB74D"));
        return sun;
    }

    /** 天空掉落的太阳（更大价值，更快下落） */
    public static SunEntity fromSky(double x) {
        SunEntity sun = new SunEntity(50);
        sun.addComponent(TransformComponent.at(x, -30));
        var mv = new MovementComponent(60);
        mv.setVelocity(0, 60);
        sun.addComponent(mv);
        sun.addComponent(new RenderComponent(RenderComponent.ShapeType.CIRCLE, 28, 28, "#FFB74D"));
        return sun;
    }
}