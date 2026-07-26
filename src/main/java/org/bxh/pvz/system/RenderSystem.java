package org.bxh.pvz.system;

import org.bxh.pvz.component.RenderComponent;
import org.bxh.pvz.component.TransformComponent;
import org.bxh.pvz.core.GameRenderer;
import org.bxh.pvz.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * Walks every active entity that carries both {@link TransformComponent}
 * and {@link RenderComponent}, delegating to {@link GameRenderer} for
 * the actual draw calls.
 */
public final class RenderSystem implements GameSystem {

    private final GameRenderer renderer;

    public RenderSystem(GameRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;

            var transform = entity.getComponent(TransformComponent.class);
            var render = entity.getComponent(RenderComponent.class);
            if (transform.isEmpty() || render.isEmpty()) continue;

            var t = transform.get();
            var r = render.get();
            if (!r.visible()) continue;

            switch (r.shapeType()) {
                case CIRCLE -> renderer.drawCircle(
                        t.x(), t.y(), r.width() / 2, r.colorHex());
                case RECT -> renderer.drawRect(
                        t.x() - r.width() / 2, t.y() - r.height() / 2,
                        r.width(), r.height(), r.colorHex());
                case ROUNDED_RECT -> renderer.drawRoundedRect(
                        t.x() - r.width() / 2, t.y() - r.height() / 2,
                        r.width(), r.height(), r.colorHex());
            }
        }
    }
}
