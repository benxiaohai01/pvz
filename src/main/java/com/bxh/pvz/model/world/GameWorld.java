package com.bxh.pvz.model.world;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.GameObject;
import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.level.Level;
import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.util.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 游戏世界：持有草坪、植物、僵尸、子弹、阳光与小推车，并驱动每帧更新。
 * 纯领域模型，不依赖 JavaFX。
 */
public final class GameWorld implements GameWorldView {

    private final Lawn lawn;
    private final Level level;
    /** 场上仍参与游戏或等待清理的僵尸。 */
    private final List<Zombie> zombies = new ArrayList<>();
    /** 场上仍参与游戏或等待清理的子弹。 */
    private final List<Projectile> projectiles = new ArrayList<>();
    /** 场上仍可收集或等待清理的阳光。 */
    private final List<Sun> suns = new ArrayList<>();
    /** 每一行的小推车，触发后会从列表中清理。 */
    private final List<LawnCar> cars = new ArrayList<>();
    /** 玩家当前可支配的阳光数量。 */
    private int availableSun;
    /** 本局是否已经结束，结束后所有实体更新都会被跳过。 */
    private boolean gameOver;

    public GameWorld(LevelConfig levelConfig) {
        // 创建固定草坪和关卡状态，再按行初始化防线小推车。
        this.lawn = new Lawn(GameConfig.GRID_ROWS, GameConfig.GRID_COLS);
        this.level = new Level(levelConfig);
        this.availableSun = levelConfig.initialSun();
        for (int row = 0; row < lawn.rows(); row++) {
            LawnCar car = new LawnCar(row);
            car.placeAtRow(lawn.rowCenterY(row));
            cars.add(car);
        }
    }

    public Lawn lawn() {
        return lawn;
    }

    public Level level() {
        return level;
    }

    public List<Zombie> zombies() {
        return Collections.unmodifiableList(zombies);
    }

    public List<Projectile> projectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    public List<Sun> suns() {
        return Collections.unmodifiableList(suns);
    }

    public List<LawnCar> cars() {
        return Collections.unmodifiableList(cars);
    }

    public List<Plant> plants() {
        return lawn.grid().plants();
    }

    public List<Plant> plantsInRow(int row) {
        return plants().stream().filter(plant -> plant.row() == row).toList();
    }

    public int sun() {
        return availableSun;
    }

    public void addSun(int amount) {
        availableSun += amount;
    }

    /** 阳光不足时拒绝扣除，保证一次种植操作不会被部分执行。 */
    public boolean spendSun(int amount) {
        if (availableSun < amount) {
            return false;
        }
        availableSun -= amount;
        return true;
    }

    /** 判断目标格子在棋盘内、尚未被占用且阳光足够。 */
    public boolean canPlant(int row, int col, int cost) {
        return lawn.grid().inBounds(row, col)
                && !lawn.grid().isOccupied(row, col)
                && availableSun >= cost;
    }

    /** 把植物放入网格，成功后同步植物在世界坐标系中的中心位置。 */
    public boolean placePlant(Plant plant) {
        if (!lawn.grid().place(plant)) {
            return false;
        }
        plant.setCellPosition(lawn.colCenterX(plant.col()), lawn.rowCenterY(plant.row()));
        return true;
    }

    public Plant removePlant(Plant plant) {
        Plant removed = lawn.grid().remove(plant.row(), plant.col());
        if (removed != null) {
            removed.markRemoved();
        }
        return removed;
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void addSun(Sun sun) {
        suns.add(sun);
    }

    public Sun findSunAt(double x, double y) {
        return suns.stream()
                .filter(sun -> !sun.isRemoved())
                .filter(sun -> sun.position().distanceTo(new Vector2(x, y))
                        <= GameConfig.SUN_RADIUS + GameConfig.SUN_COLLECT_PADDING)
                .findFirst()
                .orElse(null);
    }

    /** 收集指定阳光，先把数值加到账户并发布移除状态。 */
    public int collectSun(Sun sun) {
        suns.remove(sun);
        sun.markRemoved();
        addSun(sun.value());
        return sun.value();
    }

    public boolean isOver() {
        return gameOver;
    }

    public void markOver() {
        gameOver = true;
    }

    /** 胜负规则归属世界：所有波次生成完且场上没有存活僵尸。 */
    public boolean isWinConditionMet() {
        return level.allWavesSpawned()
                && zombies.stream().noneMatch(zombie -> !zombie.isRemoved());
    }

    /**
     * 游戏逻辑更新：按植物、僵尸、子弹、阳光、小推车的固定顺序推进。
     * 本阶段只标记死亡对象，不直接删除集合元素；清理统一放在帧末执行。
     */
    public void update(double delta) {
        // 植物更新可能产生阳光，因此先快照植物集合再遍历，避免同时修改集合。
        for (Plant plant : plants()) {
            if (!plant.isRemoved()) {
                plant.update(this, delta);
            }
        }
        for (Zombie zombie : zombies) {
            if (!zombie.isRemoved()) {
                zombie.update(this, delta);
            }
        }
        for (Projectile projectile : projectiles) {
            if (!projectile.isRemoved()) {
                projectile.update(this, delta);
            }
        }
        for (Sun sun : suns) {
            if (!sun.isRemoved()) {
                sun.update(this, delta);
            }
        }
        for (LawnCar car : cars) {
            if (!car.isRemoved()) {
                car.update(this, delta);
            }
        }
    }

    /** 帧末清理所有已标记移除的对象，并把死亡植物占用的格子释放出来。 */
    public void cleanup() {
        zombies.removeIf(GameObject::isRemoved);
        projectiles.removeIf(GameObject::isRemoved);
        suns.removeIf(GameObject::isRemoved);
        cars.removeIf(GameObject::isRemoved);
        lawn.grid().clearRemoved();
    }

}
