package com.bxh.pvz.controller;

import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.GameOverEvent;
import com.bxh.pvz.event.GameResult;
import com.bxh.pvz.event.PlantRemovalCause;
import com.bxh.pvz.event.PlantRemovedEvent;
import com.bxh.pvz.event.SunCollectedEvent;
import com.bxh.pvz.event.ZombieDeathEvent;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;
import com.bxh.pvz.model.world.GameWorldView;
import com.bxh.pvz.service.CollisionService;
import com.bxh.pvz.service.CombatService;
import com.bxh.pvz.service.SpawnService;

import java.util.List;

/**
 * 游戏控制器：组织每帧更新，并处理种植、铲除与收集阳光等玩家操作。
 * 自身不写游戏规则（规则在领域模型与服务中）。
 */
public final class GameController {

    private final GameWorld world;
    /** 本局允许玩家选择的植物类型，卡片由视图层根据该列表创建。 */
    private final List<PlantType> availablePlants;
    private final EventBus eventBus;
    private final PlantFactory plantFactory;
    private final PlantCatalog plantCatalog;
    private final CombatService combatService;
    private final CollisionService collisionService;
    private final SpawnService spawnService;
    private final PlantCooldowns cooldowns = new PlantCooldowns();
    /** 铲子模式是否开启，开启后点击草坪格会铲除其中的植物。 */
    private boolean shovelMode;
    /** 本局累计击杀的僵尸数量。 */
    private int killCount;
    /** 本局是否已经进入胜利或失败结算。 */
    private boolean gameFinished;
    /** 订阅事件总线所用的句柄，销毁时用于解除订阅。 */
    private final EventBus.Subscriber eventSubscriber;

    public GameController(
            GameWorld world,
            List<PlantType> availablePlants,
            EventBus eventBus,
            PlantFactory plantFactory,
            PlantCatalog plantCatalog,
            CombatService combatService,
            CollisionService collisionService,
            SpawnService spawnService) {
        this.world = world;
        this.availablePlants = List.copyOf(availablePlants);
        this.eventBus = eventBus;
        this.plantFactory = plantFactory;
        this.plantCatalog = plantCatalog;
        this.combatService = combatService;
        this.collisionService = collisionService;
        this.spawnService = spawnService;
        this.eventSubscriber = this::onEvent;
        eventBus.subscribe(eventSubscriber);
    }

    public void dispose() {
        eventBus.unsubscribe(eventSubscriber);
    }

    /** 每帧按“生成、实体行为、碰撞、清理、胜负判定”的顺序推进游戏。 */
    public void update(double delta) {
        if (world.isOver() || gameFinished) {
            return;
        }
        spawnService.update(world, delta);
        world.update(delta);
        collisionService.update(world, combatService, delta);
        world.cleanup();
        cooldowns.tick(delta);
        checkWin();
    }

    /**
     * 判断当前是否满足胜利条件，避免每帧重复发布游戏结束事件。
     */
    private void checkWin() {
        if (gameFinished || world.isOver()) {
            return;
        }
        if (world.isWinConditionMet()) {
            gameFinished = true;
            eventBus.publish(new GameOverEvent(GameResult.WIN));
        }
    }

    /** 切换铲子模式，再次点击铲子按钮可关闭。 */
    public void toggleShovel() {
        if (world.isOver()) {
            return;
        }
        shovelMode = !shovelMode;
    }

    /**
     * 铲除指定网格中的植物，并发布植物移除事件。
     */
    public void removePlantAt(int row, int col) {
        if (world.isOver()) {
            return;
        }
        Plant removedPlant = world.lawn().grid().plantAt(row, col);
        if (removedPlant != null && !removedPlant.isRemoved()) {
            world.removePlant(removedPlant);
            eventBus.publish(new PlantRemovedEvent(removedPlant, PlantRemovalCause.SHOVEL));
        }
    }

    /**
     * 判断植物卡片当前是否可以开始拖拽。
     */
    public boolean canStartPlantDrag(PlantType type) {
        if (world.isOver()) {
            return false;
        }
        PlantConfig plantConfig = plantCatalog.of(type);
        return cooldownRemaining(type) <= 0 && world.sun() >= plantConfig.cost();
    }

    /**
     * 尝试在指定网格放置植物；成功后才扣除阳光并启动冷却。
     */
    public boolean placePlantAt(PlantType type, int row, int col) {
        if (!canStartPlantDrag(type)) {
            return false;
        }
        PlantConfig plantConfig = plantCatalog.of(type);
        if (!world.canPlant(row, col, plantConfig.cost())) {
            return false;
        }
        if (!world.spendSun(plantConfig.cost())) {
            return false;
        }

        Plant placedPlant = plantFactory.create(type, row, col);
        if (!world.placePlant(placedPlant)) {
            // 工厂已成功创建但网格放置失败时，必须退还已经扣除的阳光。
            world.addSun(plantConfig.cost());
            return false;
        }

        cooldowns.start(type, plantConfig.cooldown());
        return true;
    }

    /** 收集单个阳光，并把收集结果发布给视图或统计系统。 */
    public void collectSun(Sun sun) {
        if (world.isOver()) {
            return;
        }
        int amount = world.collectSun(sun);
        eventBus.publish(new SunCollectedEvent(sun, amount));
    }

    /** 尝试收集画布坐标处的阳光，成功返回 true。 */
    public boolean collectSunAt(double canvasX, double canvasY) {
        Sun sun = world.findSunAt(canvasX, canvasY);
        if (sun == null) {
            return false;
        }
        collectSun(sun);
        return true;
    }

    public boolean isCellInBounds(int row, int col) {
        return world.lawn().grid().inBounds(row, col);
    }

    private void onEvent(GameEvent event) {
        switch (event) {
            case ZombieDeathEvent zombieDeathEvent -> killCount++;
            case GameOverEvent gameOverEvent -> gameFinished = true;
            default -> {
                // 其余事件由视图等订阅者处理
            }
        }
    }

    public GameWorldView world() {
        return world;
    }

    /** 把本局可选植物转换为不携带领域行为的视图展示数据。 */
    public List<PlantOption> plantOptions() {
        return availablePlants.stream()
                .map(plantCatalog::of)
                .map(PlantOption::from)
                .toList();
    }

    public boolean shovelMode() {
        return shovelMode;
    }

    public int killCount() {
        return killCount;
    }

    public double cooldownRemaining(PlantType type) {
        return cooldowns.remaining(type);
    }
}
