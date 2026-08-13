package com.bxh.pvz.core;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.config.ZombieCatalog;
import com.bxh.pvz.controller.GameController;
import com.bxh.pvz.controller.GameSessionStarter;
import com.bxh.pvz.controller.LevelSelectController;
import com.bxh.pvz.controller.MenuController;
import com.bxh.pvz.controller.MouseController;
import com.bxh.pvz.controller.PlantSelectController;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.GameOverEvent;
import com.bxh.pvz.event.GameResult;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.factory.ZombieFactory;
import com.bxh.pvz.model.world.GameWorld;
import com.bxh.pvz.renderer.GameRenderer;
import com.bxh.pvz.renderer.SpriteCatalog;
import com.bxh.pvz.service.CollisionService;
import com.bxh.pvz.service.CombatService;
import com.bxh.pvz.service.LevelService;
import com.bxh.pvz.service.SpawnService;
import com.bxh.pvz.state.GameState;
import com.bxh.pvz.state.GameStateManager;
import com.bxh.pvz.view.GameView;
import com.bxh.pvz.view.LevelSelectView;
import com.bxh.pvz.view.MenuView;
import com.bxh.pvz.view.PlantSelectView;
import com.bxh.pvz.view.ResultView;
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
    private final PlantCatalog plantCatalog = new PlantCatalog();
    private final ZombieCatalog zombieCatalog = new ZombieCatalog();
    private final SpriteCatalog spriteCatalog;
    private final LevelCatalog levelCatalog;
    private final LevelService levelService;
    private final PlantFactory plantFactory;
    private final ZombieFactory zombieFactory;
    private final CombatService combatService;
    private final CollisionService collisionService;
    private final SpawnService spawnService;
    private final GameRenderer renderer;
    private final GameLoop gameLoop;

    private final MenuView menuView;
    private final LevelSelectView levelSelectView;
    private final ResultView winView;
    private final ResultView loseView;

    private GameController gameController;
    private GameView gameView;

    public GameEngine(Stage stage) {
        this.stage = stage;
        this.levelCatalog = new LevelCatalog(plantCatalog, zombieCatalog);
        this.levelService = new LevelService(levelCatalog);
        this.plantFactory = new PlantFactory(plantCatalog);
        this.zombieFactory = new ZombieFactory(zombieCatalog);
        this.spriteCatalog = new SpriteCatalog(plantCatalog);
        this.renderer = new GameRenderer(spriteCatalog);
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
        this.levelSelectView = new LevelSelectView(new LevelSelectController(stateManager, levelService));
        this.winView = new ResultView(GameResult.WIN, () -> stateManager.transitionTo(GameState.MENU));
        this.loseView = new ResultView(GameResult.LOSE, () -> stateManager.transitionTo(GameState.MENU));

        stateManager.addListener(this::onStateChanged);
        eventBus.subscribe(this::onGameEvent);
    }

    public void start() {
        stage.setTitle(UiConfig.TITLE);
        stage.setResizable(false);
        stage.setScene(new Scene(menuView.getRoot(), UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT));
        stage.show();
    }

    @Override
    public void startGame(LevelConfig level, List<PlantType> selectedPlants) {
        disposeGameSession();

        GameWorld world = new GameWorld(level);
        gameController = new GameController(
                world, selectedPlants, eventBus,
                plantFactory, plantCatalog, combatService, collisionService, spawnService);
        MouseController mouseController = new MouseController(gameController);
        gameView = new GameView(gameController, eventBus, renderer, mouseController, spriteCatalog);

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
                PlantSelectController controller = new PlantSelectController(
                        stateManager, levelService, plantCatalog, this);
                stage.getScene().setRoot(new PlantSelectView(controller, spriteCatalog).getRoot());
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
            case GameOverEvent e -> stateManager.transitionTo(switch (e.result()) {
                case WIN -> GameState.WIN;
                case LOSE -> GameState.LOSE;
            });
            default -> {
                // 其余事件由对应订阅者处理
            }
        }
    }
}
