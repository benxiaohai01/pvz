package com.bxh.pvz.controller;

import com.bxh.pvz.command.CommandHistory;
import com.bxh.pvz.command.GameCommand;
import com.bxh.pvz.command.PlantCommand;
import com.bxh.pvz.command.RemovePlantCommand;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.GameOverEvent;
import com.bxh.pvz.event.GameResult;
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
 * 游戏控制器：组织每帧更新，并把玩家操作封装为命令执行。
 * 自身不写游戏规则（规则在领域模型与服务中）。
 */
public final class GameController {

    private final GameWorld world;
    private final List<PlantType> availablePlants;
    private final EventBus eventBus;
    private final PlantFactory plantFactory;
    private final PlantCatalog plantCatalog;
    private final CombatService combatService;
    private final CollisionService collisionService;
    private final SpawnService spawnService;
    private final PlantCooldowns cooldowns = new PlantCooldowns();
    private PlantType selectedPlant;
    private boolean shovelMode;
    private int killCount;
    private boolean finished;
    private final EventBus.Subscriber eventSubscriber;
    private final CommandHistory history = new CommandHistory(50);

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

    /** 每帧更新：生成 → 行为 → 碰撞 → 清理 → 胜负判定。 */
    public void update(double delta) {
        if (world.isOver() || finished) {
            return;
        }
        spawnService.update(world, delta);
        world.update(delta);
        collisionService.update(world, combatService, delta);
        world.cleanup();
        cooldowns.tick(delta);
        checkWin();
    }

    private void checkWin() {
        if (finished || world.isOver()) {
            return;
        }
        if (world.isWinConditionMet()) {
            finished = true;
            eventBus.publish(new GameOverEvent(GameResult.WIN));
        }
    }

    public void selectPlant(PlantType type) {
        if (world.isOver()) {
            return;
        }
        shovelMode = false;
        PlantConfig config = plantCatalog.of(type);
        if (cooldownRemaining(type) > 0 || world.sun() < config.cost()) {
            return;
        }
        selectedPlant = selectedPlant == type ? null : type;
    }

    public void toggleShovel() {
        if (world.isOver()) {
            return;
        }
        selectedPlant = null;
        shovelMode = !shovelMode;
    }

    public void onCellClicked(int row, int col) {
        if (world.isOver()) {
            return;
        }
        if (shovelMode) {
            Plant plant = world.lawn().grid().plantAt(row, col);
            if (plant != null) {
                run(new RemovePlantCommand(world, eventBus, plant));
            }
            return;
        }
        if (selectedPlant == null || cooldownRemaining(selectedPlant) > 0) {
            return;
        }
        PlantType type = selectedPlant;
        PlantConfig config = plantCatalog.of(type);
        if (run(new PlantCommand(world, plantFactory, type, row, col, config.cost()))) {
            cooldowns.start(type, config.cooldown());
            selectedPlant = null;
        }
    }

    public void collectSun(Sun sun) {
        if (world.isOver()) {
            return;
        }
        int amount = world.collectSun(sun);
        eventBus.publish(new SunCollectedEvent(sun, amount));
    }

    public boolean collectSunAt(double x, double y) {
        Sun sun = world.findSunAt(x, y);
        if (sun == null) {
            return false;
        }
        collectSun(sun);
        return true;
    }

    public boolean isCellInBounds(int row, int col) {
        return world.lawn().grid().inBounds(row, col);
    }

    private boolean run(GameCommand command) {
        boolean executed = command.canExecute() && command.execute();
        if (executed) {
            history.push(command);
        }
        return executed;
    }

    /** 撤销最近一次成功执行的命令（种植/铲除）。 */
    public boolean undoLast() {
        if (world.isOver() || finished) {
            return false;
        }
        return history.undo();
    }

    private void onEvent(GameEvent event) {
        switch (event) {
            case ZombieDeathEvent e -> killCount++;
            case GameOverEvent e -> finished = true;
            default -> {
                // 其余事件由视图等订阅者处理
            }
        }
    }

    public GameWorldView world() {
        return world;
    }

    public List<PlantOption> plantOptions() {
        return availablePlants.stream()
                .map(plantCatalog::of)
                .map(PlantOption::from)
                .toList();
    }

    public PlantType selectedPlant() {
        return selectedPlant;
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
