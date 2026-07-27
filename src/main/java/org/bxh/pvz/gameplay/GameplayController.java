package org.bxh.pvz.gameplay;

import org.bxh.pvz.ecs.entity.PlantEntity;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.world.GameWorld;
import org.bxh.pvz.world.GridMap;

/**
 * 游戏玩法控制器 —— 处理植物种植事件、僵尸波次生成。
 * 后续扩展：阳光资源、关卡进度、胜负判定。
 */
public final class GameplayController {

    private final GameWorld world;
    private final EventBus eventBus;
    private final GridMap gridMap;
    private double zombieSpawnTimer;

    public GameplayController(GameWorld world, EventBus eventBus, GridMap gridMap) {
        this.world = world;
        this.eventBus = eventBus;
        this.gridMap = gridMap;
        this.zombieSpawnTimer = 3.0;
        subscribeEvents();
    }

    /** 订阅游戏事件 */
    private void subscribeEvents() {
        eventBus.subscribe(GameEvent.PlantPlaced.class, this::onPlantPlaced);
    }

    /** 处理种植事件：根据植物类型创建对应实体 */
    private void onPlantPlaced(GameEvent.PlantPlaced event) {
        if (event.plantType() == null) return;

        double x = gridMap.cellToScreenX(event.col());
        double y = gridMap.cellToScreenY(event.row());

        PlantEntity plant = switch (event.plantType()) {
            case "peashooter" -> PlantEntity.createPeashooter(event.row(), event.col(), x, y);
            case "sunflower" -> PlantEntity.createSunflower(event.row(), event.col(), x, y);
            case "wallnut" -> PlantEntity.createWallNut(event.row(), event.col(), x, y);
            default -> null;
        };

        if (plant != null) {
            world.spawnEntity(plant);
        }
    }

    /**
     * 每帧调用：更新僵尸生成计时器。
     * 周期性地在右侧随机行生成普通僵尸。
     */
    public void update(double deltaTime) {
        zombieSpawnTimer -= deltaTime;
        if (zombieSpawnTimer <= 0) {
            spawnZombie();
            zombieSpawnTimer = 6.0 + Math.random() * 4.0;
        }
    }

    /** 在右侧随机行生成一个普通僵尸 */
    private void spawnZombie() {
        int row = (int) (Math.random() * gridMap.rows());
        double x = gridMap.cellToScreenX(gridMap.cols() - 1);
        double y = gridMap.cellToScreenY(row);
        world.spawnEntity(ZombieEntity.createBasicZombie(x, y));
    }
}