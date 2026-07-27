package org.bxh.pvz.core;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.ecs.system.CollisionSystem;
import org.bxh.pvz.ecs.system.CombatSystem;
import org.bxh.pvz.ecs.system.GameSystem;
import org.bxh.pvz.ecs.system.MovementSystem;
import org.bxh.pvz.ecs.system.RenderSystem;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.gameplay.GameplayController;
import org.bxh.pvz.input.InputManager;
import org.bxh.pvz.world.GameWorld;
import org.bxh.pvz.world.GridMap;

import java.util.List;

/**
 * 【设计模式：门面模式（Facade）—— 对上层屏蔽内部 ECS/事件/输入的复杂性】
 * 【设计模式：依赖注入（DI）—— 构造函数手动注入所有依赖】
 * 游戏根编排器。组装 ECS 世界、事件总线、输入、系统、渲染器与游戏循环。
 */
public final class Game {

    private final GameConfig config;
    private final GameWorld world;
    private final EventBus eventBus;
    private final InputManager inputManager;
    private final GameRenderer renderer;
    private final GameLoop gameLoop;
    private final GameplayController gameplayController;

    private final List<GameSystem> logicSystems;
    private final RenderSystem renderSystem;

    public Game(Canvas canvas, GameConfig config) {
        this.config = config;

        var gridMap = new GridMap(config);
        this.world = new GameWorld(gridMap);
        this.eventBus = new EventBus();
        this.inputManager = new InputManager(config, eventBus, gridMap);
        this.renderer = new GameRenderer(canvas, config);
        this.renderSystem = new RenderSystem(renderer);
        this.gameplayController = new GameplayController(world, eventBus, gridMap);

        this.logicSystems = List.of(
                new MovementSystem(),
                new CombatSystem(),
                new CollisionSystem());

        this.gameLoop = new GameLoop(this);
    }

    /** 绑定 JavaFX 输入到输入管理器，随后启动游戏循环 */
    public void start(Scene scene) {
        inputManager.attachToScene(scene);
        System.out.println("Game Started");
        gameLoop.start();
    }

    /** 逻辑帧：事件分发 -> 输入处理 -> 游戏玩法 -> 实体队列 -> 系统更新 */
    void update(double deltaTime) {
        eventBus.dispatch();
        inputManager.processPending();
        gameplayController.update(deltaTime);
        world.processPending();
        logicSystems.forEach(s -> s.update(deltaTime, world));
    }

    /** 渲染帧：清屏 -> 顶部栏 -> 网格 -> 实体 -> 卡片 -> 拖拽幽灵 */
    void render() {
        renderer.clear();
        renderer.drawTopBar();
        renderer.drawGrid(world.gridMap());
        renderSystem.update(0, world);
        renderer.drawPlantCards(inputManager.cards());

        if (inputManager.dragging()) {
            renderer.drawDragGhost(inputManager.dragPlantType(),
                    inputManager.mouseX(), inputManager.mouseY());
        }
    }

    public GameWorld world() { return world; }
    public EventBus eventBus() { return eventBus; }
    public InputManager inputManager() { return inputManager; }
}