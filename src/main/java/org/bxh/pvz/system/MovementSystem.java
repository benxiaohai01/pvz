package org.bxh.pvz.system;

import org.bxh.pvz.component.MovementComponent;
import org.bxh.pvz.component.TransformComponent;
import org.bxh.pvz.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * Updates entity positions based on {@link MovementComponent} velocity.
 * Since {@link TransformComponent} is an immutable record, each update
 * replaces it with a new instance on the entity.
 */
public final class MovementSystem implements GameSystem {

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;

            var movement = entity.getComponent(MovementComponent.class);
            var transform = entity.getComponent(TransformComponent.class);
            if (movement.isEmpty() || transform.isEmpty()) continue;

            var m = movement.get();
            var t = transform.get();

            double newX = t.x() + m.velocityX() * deltaTime;
            double newY = t.y() + m.velocityY() * deltaTime;

            entity.addComponent(t.withPosition(newX, newY));
        }
    }
}
