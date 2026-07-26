package org.bxh.pvz.core;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.input.InputManager;
import org.bxh.pvz.system.CollisionSystem;
import org.bxh.pvz.system.CombatSystem;
import org.bxh.pvz.system.GameSystem;
import org.bxh.pvz.system.MovementSystem;
import org.bxh.pvz.system.RenderSystem;
import org.bxh.pvz.world.GameWorld;
import org.bxh.pvz.world.GridMap;

import java.util.List;

/**
 * Root game orchestrator. Wires together the ECS world, event bus,
 * input, systems, renderer, and the game loop.
 * <p>
 * Ownership model (top-down):
 * <pre>
 *   GameApplication -> Game -> GameLoop
 *                           -> GameWorld
 *                           -> EventBus
 *                           -> InputManager
 *                           -> GameRenderer
 *                           -> [MovementSystem, CombatSystem, CollisionSystem]
 *                           -> RenderSystem (called during render phase)
 * </pre>
 */
public final class Game {

    private final GameConfig config;
    private final GameWorld world;
    private final EventBus eventBus;
    private final InputManager inputManager;
    private final GameRenderer renderer;
    private final GameLoop gameLoop;

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

        this.logicSystems = List.of(
                new MovementSystem(),
                new CombatSystem(),
                new CollisionSystem());

        this.gameLoop = new GameLoop(this);
    }

    // -- Public API ---------------------------------------------------------

    /** Wire JavaFX input to the input manager, then start the loop. */
    public void start(Scene scene) {
        inputManager.attachToScene(scene);
        System.out.println("Game Started");
        gameLoop.start();
    }

    // -- Package-private: called by GameLoop ---------------------------------

    void update(double deltaTime) {
        eventBus.dispatch();
        inputManager.processPending();
        world.processPending();

        for (var system : logicSystems) {
            system.update(deltaTime, world);
        }
    }

    void render() {
        renderer.clear();
        renderer.drawGrid(world.gridMap());
        renderSystem.update(0, world); // delta unused for rendering
    }

    // -- Accessors ----------------------------------------------------------

    public GameWorld world() { return world; }
    public EventBus eventBus() { return eventBus; }
    public InputManager inputManager() { return inputManager; }
}
