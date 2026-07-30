package org.bxh.pvz.core;

import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.system.*;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.input.InputManager;
import org.bxh.pvz.world.GameWorld;
import java.util.List;

public final class Game {
    private final GameWorld world;
    private final EventBus eventBus;
    private final InputManager inputManager;
    private final List<GameSystem> logicSystems;

    public Game(GameConfig config, GameWorld world, EventBus eventBus,
                InputManager inputManager, SunSystem sunSystem,
                WaveSystem waveSystem, GameOverSystem gameOverSystem) {
        this.world = world; this.eventBus = eventBus; this.inputManager = inputManager;
        this.logicSystems = List.of(
                new MovementSystem(), new CombatSystem(), new CollisionSystem(),
                sunSystem, waveSystem, gameOverSystem, new LawnMowerSystem());
    }

    public void update(double deltaTime) {
        eventBus.dispatch();
        inputManager.processPending();
        world.processPending();
        logicSystems.forEach(s -> s.update(deltaTime, world));
    }

    public void render(GameRenderer renderer) {
        renderer.clear();
        renderer.drawTopBar();
        renderer.drawGrid(world.gridMap());

        // 直接遍历实体绘制到外部 renderer（不使用独立的 RenderSystem 内部 Canvas）
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;
            entity.getComponent(TransformComponent.class).ifPresent(transform ->
                entity.getComponent(RenderComponent.class)
                      .filter(RenderComponent::visible)
                      .ifPresent(r -> {
                          switch (r.shapeType()) {
                              case CIRCLE -> renderer.drawCircle(
                                      transform.x(), transform.y(), r.width() / 2, r.colorHex());
                              case RECT -> renderer.drawRect(
                                      transform.x() - r.width() / 2, transform.y() - r.height() / 2,
                                      r.width(), r.height(), r.colorHex());
                              case ROUNDED_RECT -> renderer.drawRoundedRect(
                                      transform.x() - r.width() / 2, transform.y() - r.height() / 2,
                                      r.width(), r.height(), r.colorHex());
                          }
                      }));
        }

        renderer.drawPlantCards(inputManager.cards());
        if (inputManager.dragging()) {
            renderer.drawDragGhost(inputManager.dragPlantType(),
                    inputManager.mouseX(), inputManager.mouseY());
        }
    }
}