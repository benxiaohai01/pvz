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

    /**
     * 普通僵尸 —— 竖立红色长方体 26x54px。
     * 生命130、速度55px/s、近战伤害20、冷却1.0s。
     * 数值设计：单发豌豆射手打不死一只僵尸，僵尸会冲到植物面前攻击，
     * 需要前排坚果墙或双发射手才能安全击杀。
     */
    public static ZombieEntity createBasicZombie(double x, double y) {
        ZombieEntity entity = new ZombieEntity("basic");
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(130));
        entity.addComponent(new MovementComponent(55));
        entity.addComponent(new AttackComponent(20, 50, 1.0));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.RECT, 26, 54, "#C62828"));
        return entity;
    }

    /** 路障僵尸工厂 */
    public static ZombieEntity createConeZombie(double x, double y) {
        ZombieEntity entity = new ZombieEntity("cone");
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(250));
        entity.addComponent(new MovementComponent(50));
        entity.addComponent(new AttackComponent(20, 50, 1.0));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.RECT, 26, 54, "#FF6F00"));
        return entity;
    }
}