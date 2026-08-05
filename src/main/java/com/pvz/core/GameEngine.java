package com.pvz.core;

import com.pvz.config.GameConfig;
import com.pvz.controller.GameController;
import com.pvz.controller.GameSessionStarter;
import com.pvz.controller.LevelSelectController;
import com.pvz.controller.MenuController;
import com.pvz.controller.MouseController;
import com.pvz.controller.PlantSelectController;
import com.pvz.event.EventBus;
import com.pvz.event.GameEvent;
import com.pvz.event.GameOverEvent;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.level.LevelConfig;
import com.pvz.model.world.GameWorld;
import com.pvz.renderer.GameRenderer;
import com.pvz.service.CollisionService;
import com.pvz.service.CombatService;
import com.pvz.service.LevelService;
import com.pvz.service.SpawnService;
import com.pvz.view.GameView;
import com.pvz.view.LevelSelectView;
import com.pvz.view.MenuView;
import com.pvz.view.PlantSelectView;
import com.pvz.view.ResultView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * 游戏引擎：负责场景切换、游戏循环与各模块装配（组合根）。
 * 不包含具体游戏规则，规则在模型/服务/命令中。
 */
public final class GameEngine implements GameSessionStarter {

    private final Stage stage;
    private final GameStateManager stateManager = new GameStateManager();
    private final EventBus eventBus = new EventBus();
    private final LevelService levelService = new LevelService();
    private final PlantFactory plantFactory = new PlantFactory();
    private final ZombieFactory zombieFactory = new ZombieFactory();
    private final CombatService combatService;
    private final CollisionService collisionService;
    private final SpawnService spawnService;
    private final GameRenderer renderer = new GameRenderer();
    private final GameLoop gameLoop;

    private final MenuView menuView;
    private final LevelSelectView levelSelectView;
    private final ResultView winView;
    private final ResultView loseView;

    private GameController gameController;
    private GameView gameView;

    public GameEngine(Stage stage) {
        this.stage = stage;
        this.combatService = new CombatService(eventBus);
        this.collisionService = new CollisionService(eventBus);
        this.spawnService = new SpawnService(zombieFactory, eventBus);

        this.gameLoop = new GameLoop(new GameLoop.Listener() {
            @Override
            public void update(double deltaSeconds) {
                if (stateManager.current() == GameState.PLAYING && gameController != null) {
                    gameController.update(deltaSeconds);
                }
            }

            @Override
            public void render() {
                if (stateManager.current() == GameState.PLAYING && gameView != null && gameController != null) {
                    gameView.refresh(gameController, gameLoop.elapsedTime(), gameLoop.lastDelta());
                }
            }
        });

        this.menuView = new MenuView(new MenuController(stateManager));
        this.levelSelectView = new LevelSelectView(new LevelSelectController(stateManager, levelService), levelService);
        this.winView = new ResultView(GameState.WIN, () -> stateManager.transitionTo(GameState.MENU));
        this.loseView = new ResultView(GameState.LOSE, () -> stateManager.transitionTo(GameState.MENU));

        stateManager.addListener(this::onStateChanged);
        eventBus.subscribe(this::onGameEvent);
    }

    public void start() {
        stage.setTitle(GameConfig.TITLE);
        stage.setResizable(false);
        stage.setScene(new Scene(menuView.getRoot(), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        stage.show();
    }

    @Override
    public void startGame(LevelConfig level, List<PlantType> selectedPlants) {
        disposeGameSession();

        GameWorld world = new GameWorld(level);
        gameController = new GameController(
                world, selectedPlants, eventBus,
                plantFactory, combatService, collisionService, spawnService);
        MouseController mouseController = new MouseController(gameController);
        gameView = new GameView(gameController, selectedPlants, eventBus, renderer, mouseController);

        stateManager.transitionTo(GameState.PLAYING);
    }

    private void onStateChanged(GameState state) {
        switch (state) {
            case MENU -> {
                disposeGameSession();
                stage.getScene().setRoot(menuView.getRoot());
            }
            case LEVEL_SELECT -> stage.getScene().setRoot(levelSelectView.getRoot());
            case PLANT_SELECT -> {
                PlantSelectController controller = new PlantSelectController(stateManager, levelService, this);
                stage.getScene().setRoot(new PlantSelectView(controller).getRoot());
            }
            case PLAYING -> {
                gameLoop.start();
                stage.getScene().setRoot(gameView.getRoot());
            }
            case WIN -> {
                gameLoop.stop();
                disposeGameSession();
                stage.getScene().setRoot(winView.getRoot());
            }
            case LOSE -> {
                gameLoop.stop();
                disposeGameSession();
                stage.getScene().setRoot(loseView.getRoot());
            }
        }
    }

    private void disposeGameSession() {
        if (gameController != null) {
            gameController.dispose();
            gameController = null;
        }
        if (gameView != null) {
            gameView.dispose();
            gameView = null;
        }
    }

    private void onGameEvent(GameEvent event) {
        switch (event) {
            case GameOverEvent e -> stateManager.transitionTo(e.result());
            default -> {
                // 其余事件由对应订阅者处理
            }
        }
    }
}
