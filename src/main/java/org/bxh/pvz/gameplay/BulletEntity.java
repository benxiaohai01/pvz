package org.bxh.pvz.gameplay;

import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;

/**
 * 子弹实体 —— 由豌豆射手发射的绿色小圆形，向右高速飞行。
 * 碰撞到僵尸时造成伤害并自毁。
 * 后续替换：将 RenderComponent 改为纹理 key。
 */
public final class BulletEntity extends Entity {

    private final double damage;

    private BulletEntity(double damage) {
        this.damage = damage;
    }

    public double damage() { return damage; }

    /**
     * 创建子弹 —— 从指定位置向右发射
     * @param x      发射点 X 坐标（植物中心偏右）
     * @param y      发射点 Y 坐标（植物中心）
     * @param damage 碰撞伤害值
     * @param speed  飞行速度（像素/秒）
     */
    public static BulletEntity create(double x, double y, double damage, double speed) {
        BulletEntity bullet = new BulletEntity(damage);
        bullet.addComponent(TransformComponent.at(x, y));
        var mv = new MovementComponent(speed);
        mv.setVelocity(speed, 0); // 向右飞行
        bullet.addComponent(mv);
        bullet.addComponent(new RenderComponent(
                RenderComponent.ShapeType.CIRCLE, 8, 8, "#66BB6A"));
        return bullet;
    }
}