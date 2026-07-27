package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * 移动系统 —— 根据 MovementComponent 的速度更新实体位置。
 * 由于 TransformComponent 是不可变 record，每次更新会替换为新实例。
 */
public final class MovementSystem implements GameSystem {

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;

            entity.getComponent(TransformComponent.class).ifPresent(transform ->
                entity.getComponent(MovementComponent.class).ifPresent(movement -> {
                    entity.addComponent(transform.withPosition(
                            transform.x() + movement.velocityX() * deltaTime,
                            transform.y() + movement.velocityY() * deltaTime));
                }));
        }
    }
}