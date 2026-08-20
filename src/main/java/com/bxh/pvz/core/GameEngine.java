package com.bxh.pvz.core;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.config.ZombieCatalog;
import com.bxh.pvz.config.ZombieType;
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
import com.bxh.pvz.model.entity.zombie.Zombie;
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
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * 游戏引擎：负责场景切换、游戏循环与各模块装配（组合根）。
 * 不包含具体游戏规则，规则分散在模型和服务中，本类只负责按顺序调用。
 */
public final class GameEngine implements GameSessionStarter {

    private final Stage stage;
    /** 本地调试直达配置，正式启动时为空配置。 */
    private final DevelopmentLaunchConfig developmentLaunchConfig;
    /** 顶层流程状态机，所有场景切换都通过它发起。 */
    private final GameStateManager stateManager = new GameStateManager();
    /** 跨场景共享的事件总线，对局结束事件由它通知引擎切换胜负界面。 */
    private final EventBus eventBus = new EventBus();
    /** 只读配置目录，在启动阶段加载并建立类型索引。 */
    private final PlantCatalog plantCatalog = new PlantCatalog();
    private final ZombieCatalog zombieCatalog = new ZombieCatalog();
    /** 图片资源目录，被游戏视图和渲染器共同读取。 */
    private final SpriteCatalog spriteCatalog;
    private final LevelCatalog levelCatalog;
    /** 领域服务和工厂，属于单局可复用的无状态依赖。 */
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

    /** 当前正在运行的游戏会话，进入菜单或结算界面后会被销毁。 */
    private GameController gameController;
    private GameView gameView;

    public GameEngine(Stage stage) {
        this(stage, null);
    }

    public GameEngine(Stage stage, Application.Parameters parameters) {
        this.stage = stage;
        // 先装配只读目录、工厂和领域服务，再把它们注入渲染与控制器。
        this.levelCatalog = new LevelCatalog(plantCatalog, zombieCatalog);
        this.developmentLaunchConfig = DevelopmentLaunchConfig.from(parameters, levelCatalog);
        this.levelService = new LevelService(levelCatalog);
        this.plantFactory = new PlantFactory(plantCatalog);
        this.zombieFactory = new ZombieFactory(zombieCatalog);
        this.spriteCatalog = new SpriteCatalog(plantCatalog);
        this.renderer = new GameRenderer(spriteCatalog);
        this.combatService = new CombatService(eventBus);
        this.collisionService = new CollisionService(eventBus);
        this.spawnService = new SpawnService(zombieFactory, eventBus);

        // 游戏循环只驱动当前控制器和视图，其他界面由 JavaFX 事件直接响应。
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

        // 静态界面可以在启动时创建；动态对局视图在开始游戏时再创建。
        this.menuView = new MenuView(new MenuController(stateManager));
        this.levelSelectView = new LevelSelectView(new LevelSelectController(stateManager, levelService));
        this.winView = new ResultView(GameResult.WIN, () -> stateManager.transitionTo(GameState.MENU));
        this.loseView = new ResultView(GameResult.LOSE, () -> stateManager.transitionTo(GameState.MENU));

        // 引擎既是状态变化的观察者，也负责监听全局游戏结束事件。
        stateManager.addListener(this::onStateChanged);
        eventBus.subscribe(this::onGameEvent);
    }

    public void start() {
        stage.setTitle(UiConfig.TITLE);
        stage.setResizable(false);
        // 先放一个正式菜单场景作为容器；调试直达时会在窗口显示前被游戏页面替换。
        stage.setScene(new Scene(menuView.getRoot(), UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT));
        if (developmentLaunchConfig.directToGame()) {
            startDevelopmentGame();
        }
        stage.show();
    }

    @Override
    public void startGame(LevelConfig level, List<PlantType> selectedPlants) {
        createGameSession(level, selectedPlants, List.of());
        stateManager.transitionTo(GameState.PLAYING);
    }

    /**
     * 调试启动专用入口：使用与正式流程相同的方法创建对局，只在状态机初始化处直通。
     */
    private void startDevelopmentGame() {
        LevelConfig level = levelCatalog.byId(developmentLaunchConfig.levelId());
        levelService.selectLevel(level.id());
        createGameSession(
                level,
                developmentLaunchConfig.plantTypes(),
                developmentLaunchConfig.zombieTypes());
        stateManager.startAt(GameState.PLAYING);
    }

    /**
     * 创建一次完整游戏会话；正式流程和调试流程共用，避免维护两份装配逻辑。
     */
    private void createGameSession(
            LevelConfig level,
            List<PlantType> selectedPlants,
            List<ZombieType> previewZombies) {
        // 开始新对局前先释放上一局，避免旧的订阅者和循环继续工作。
        disposeGameSession();

        // 一次游戏会话由世界、控制器、鼠标输入与视图组成。
        GameWorld world = new GameWorld(level);
        placePreviewZombies(world, previewZombies);
        gameController = new GameController(
                world, selectedPlants, eventBus,
                plantFactory, plantCatalog, combatService, collisionService, spawnService);
        MouseController mouseController = new MouseController(gameController);
        gameView = new GameView(gameController, eventBus, renderer, mouseController, spriteCatalog);
    }

    /**
     * 把调试配置中的僵尸直接放到右侧出生点，并轮流分配到不同草坪行。
     * 开局镜头滑到道路区域时即可预览这些僵尸。
     */
    private void placePreviewZombies(GameWorld world, List<ZombieType> previewZombies) {
        for (int index = 0; index < previewZombies.size(); index++) {
            int row = index % world.lawn().rows();
            Zombie zombie = zombieFactory.create(previewZombies.get(index), row);
            // 僵尸构造时已经使用 SPAWN_X，这里只补上该行在画布中的纵坐标。
            zombie.placeAtRow(world.lawn().rowCenterY(row));
            world.addZombie(zombie);
        }
    }

    /**
     * 根据状态机结果替换场景根节点，并控制游戏循环的启动与停止。
     */
    private void onStateChanged(GameState state) {
        switch (state) {
            case MENU -> {
                // 返回主菜单意味着本局彻底结束，释放所有对局资源。
                disposeGameSession();
                stage.getScene().setRoot(menuView.getRoot());
            }
            case LEVEL_SELECT -> stage.getScene().setRoot(levelSelectView.getRoot());
            case PLANT_SELECT -> {
                // 选植物界面只存活一次选择流程，每次进入都重新创建控制器和视图。
                PlantSelectController controller = new PlantSelectController(
                        stateManager, levelService, plantCatalog, this);
                stage.getScene().setRoot(new PlantSelectView(controller, spriteCatalog).getRoot());
            }
            case PLAYING -> showPlayingGame();
            case WIN -> {
                // 结算界面不运行游戏循环，同时清理当前对局订阅。
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

    /**
     * 挂载首帧已经绘制好的游戏视图，等开局镜头回到草坪后再启动循环。
     */
    private void showPlayingGame() {
        if (stage.getScene() == null || gameView == null) {
            throw new IllegalStateException("游戏页面尚未完成装配");
        }
        stage.getScene().setRoot(gameView.getRoot());
        gameView.playIntro(gameLoop::start);
    }

    /** 统一销毁当前对局的控制器与视图，防止跨场景事件泄漏。 */
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
                // 其余事件由视图、控制器等各自订阅者处理。
            }
        }
    }
}
