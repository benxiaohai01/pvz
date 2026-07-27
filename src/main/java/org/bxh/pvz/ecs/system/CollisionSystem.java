package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.HealthComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.gameplay.BulletEntity;
import org.bxh.pvz.world.GameWorld;

/**
 * 碰撞系统 —— 子弹与僵尸的 AABB 碰撞检测。
 * 子弹命中僵尸时造成伤害并自毁，僵尸死亡时移除。
 */
public final class CollisionSystem implements GameSystem {

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity a : world.entities()) {
            if (!(a instanceof BulletEntity bullet) || !bullet.active()) continue;

            for (Entity b : world.entities()) {
                if (!(b instanceof ZombieEntity zombie) || !zombie.active()) continue;

                if (aabbOverlap(bullet, zombie)) {
                    zombie.getComponent(HealthComponent.class).ifPresent(hp -> {
                        boolean dead = hp.takeDamage(bullet.damage());
                        world.destroyEntity(bullet);
                        if (dead) world.destroyEntity(zombie);
                    });
                    break;
                }
            }
        }
    }

    /** 简易 AABB 重叠检测（圆形子弹 vs 矩形僵尸） */
    private boolean aabbOverlap(BulletEntity bullet, ZombieEntity zombie) {
        var btf = bullet.getComponent(TransformComponent.class);
        var ztf = zombie.getComponent(TransformComponent.class);
        var br = bullet.getComponent(RenderComponent.class);
        var zr = zombie.getComponent(RenderComponent.class);
        if (btf.isEmpty() || ztf.isEmpty() || br.isEmpty() || zr.isEmpty()) return false;

        double bx = btf.get().x(), by = btf.get().y();
        double zx = ztf.get().x(), zy = ztf.get().y();
        double bw = br.get().width(), bh = br.get().height();
        double zw = zr.get().width(), zh = zr.get().height();

        // 子弹用圆形近似：距离 < 半径和
        double dx = Math.abs(bx - zx);
        double dy = Math.abs(by - zy);
        return dx < (bw + zw) / 2 && dy < (bh + zh) / 2;
    }
}