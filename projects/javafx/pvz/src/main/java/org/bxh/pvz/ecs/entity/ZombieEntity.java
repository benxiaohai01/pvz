package org.bxh.pvz.ecs.entity;

import org.bxh.pvz.ecs.component.AttackComponent;
import org.bxh.pvz.ecs.component.HealthComponent;
import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;

/**
 * 【设计模式：工厂方法模式（Factory Method）—— 静态工厂创建不同僵尸类型】
 * 僵尸实体 —— 组合式构造，僵尸类型由组件参数区分。
 */
public final class ZombieEntity extends Entity {

    private final String zombieType;

    private ZombieEntity(String zombieType) {
        this.zombieType = zombieType;
    }

    public String zombieType() { return zombieType; }

    /** 普通僵尸工厂 */
    public static ZombieEntity createBasicZombie(double x, double y) {
        ZombieEntity entity = new ZombieEntity("basic");
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(100));
        entity.addComponent(new MovementComponent(40)); // 像素/秒
        entity.addComponent(new AttackComponent(15, 40, 1.0));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.RECT, 28, 50, "#795548"));
        return entity;
    }

    /** 路障僵尸工厂 */
    public static ZombieEntity createConeZombie(double x, double y) {
        ZombieEntity entity = new ZombieEntity("cone");
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(200));
        entity.addComponent(new MovementComponent(40));
        entity.addComponent(new AttackComponent(15, 40, 1.0));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.RECT, 28, 50, "#FF6F00"));
        return entity;
    }
}
