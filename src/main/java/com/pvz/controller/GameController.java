package com.pvz.controller;

import com.pvz.command.GameCommand;
import com.pvz.command.PlantCommand;
import com.pvz.command.RemovePlantCommand;
import com.pvz.config.PlantCatalog;
import com.pvz.config.PlantConfig;
import com.pvz.core.GameState;
import com.pvz.event.EventBus;
import com.pvz.event.GameEvent;
import com.pvz.event.GameOverEvent;
import com.pvz.event.SunCollectedEvent;
import com.pvz.event.ZombieDeathEvent;
import com.pvz.factory.PlantFactory;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.world.GameWorld;
import com.pvz.service.CollisionService;
import com.pvz.service.CombatService;
import com.pvz.service.SpawnService;

import java.util.EnumMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * 游戏控制器：组织每帧更新，并把玩家操作封装为命令执行。
 * 自身不写游戏规则（规则在领域模型与服务中）。
 */
public final class GameController {

    private final GameWorld world;
    private final List<PlantType> availablePlants;
    private final EventBus eventBus;
    private final PlantFactory plantFactory;
    private final CombatService combatService;
    private final CollisionService collisionService;
    private final SpawnService spawnService;
    private final Map<PlantType, Double> cooldowns = new EnumMap<>(PlantType.class);
    private PlantType selectedPlant;
    private boolean shovelMode;
    private int killCount;
    private boolean finished;
    private final EventBus.Subscriber eventSubscriber;
    private final Deque<GameCommand> history = new ArrayDeque<>();

    public GameController(
            GameWorld world,
            List<PlantType> availablePlants,
            EventBus eventBus,
            PlantFactory plantFactory,
            CombatService combatService,
            CollisionService collisionService,
            SpawnService spawnService) {
        this.world = world;
        this.availablePlants = List.copyOf(availablePlants);
        this.eventBus = eventBus;
        this.plantFactory = plantFactory;
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
        tickCooldowns(delta);
        checkWin();
    }

    private void checkWin() {
        if (finished || world.isOver()) {
            return;
        }
        if (world.isWinConditionMet()) {
            finished = true;
            eventBus.publish(new GameOverEvent(GameState.WIN));
        }
    }

    public void selectPlant(PlantType type) {
        if (world.isOver()) {
            return;
        }
        shovelMode = false;
        PlantConfig config = PlantCatalog.of(type);
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
        if (run(new PlantCommand(world, plantFactory, type, row, col))) {
            cooldowns.put(type, PlantCatalog.of(type).cooldown());
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

    private boolean run(GameCommand command) {
        boolean executed = command.canExecute() && command.execute();
        if (executed) {
            history.push(command);
            if (history.size() > 50) {
                history.removeLast();
            }
        }
        return executed;
    }

    /** 撤销最近一次成功执行的命令（种植/铲除）。 */
    public boolean undoLast() {
        if (world.isOver() || finished || history.isEmpty()) {
            return false;
        }
        history.pop().undo();
        return true;
    }

    private void tickCooldowns(double delta) {
        cooldowns.replaceAll((type, remaining) -> Math.max(0, remaining - delta));
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

    public GameWorld world() {
        return world;
    }

    public List<PlantType> availablePlants() {
        return availablePlants;
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
        return cooldowns.getOrDefault(type, 0.0);
    }
}
