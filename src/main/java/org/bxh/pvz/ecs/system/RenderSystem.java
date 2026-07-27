package org.bxh.pvz.ecs.system;

import org.bxh.pvz.core.GameRenderer;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * 渲染系统 —— 遍历所有拥有 TransformComponent + RenderComponent 的活跃实体，
 * 将绘制委托给 GameRenderer。
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

            entity.getComponent(TransformComponent.class).ifPresent(transform ->
                entity.getComponent(RenderComponent.class)
                      .filter(RenderComponent::visible)
                      .ifPresent(render -> {
                          switch (render.shapeType()) {
                              case CIRCLE -> renderer.drawCircle(
                                      transform.x(), transform.y(), render.width() / 2, render.colorHex());
                              case RECT -> renderer.drawRect(
                                      transform.x() - render.width() / 2, transform.y() - render.height() / 2,
                                      render.width(), render.height(), render.colorHex());
                              case ROUNDED_RECT -> renderer.drawRoundedRect(
                                      transform.x() - render.width() / 2, transform.y() - render.height() / 2,
                                      render.width(), render.height(), render.colorHex());
                          }
                      }));
        }
    }
}