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
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Sun> suns = new ArrayList<>();
    private final List<LawnCar> cars = new ArrayList<>();
    private int sun;
    private boolean over;

    public GameWorld(LevelConfig config) {
        this.lawn = new Lawn(GameConfig.GRID_ROWS, GameConfig.GRID_COLS);
        this.level = new Level(config);
        this.sun = config.initialSun();
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
        return plants().stream().filter(p -> p.row() == row).toList();
    }

    public int sun() {
        return sun;
    }

    public void addSun(int amount) {
        sun += amount;
    }

    public boolean spendSun(int amount) {
        if (sun < amount) {
            return false;
        }
        sun -= amount;
        return true;
    }

    public boolean canPlant(int row, int col, int cost) {
        return lawn.grid().inBounds(row, col)
                && !lawn.grid().isOccupied(row, col)
                && sun >= cost;
    }

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
                .filter(s -> !s.isRemoved())
                .filter(s -> s.position().distanceTo(new com.bxh.pvz.util.Vector2(x, y))
                        <= GameConfig.SUN_RADIUS + GameConfig.SUN_COLLECT_PADDING)
                .findFirst()
                .orElse(null);
    }

    public int collectSun(Sun sun) {
        suns.remove(sun);
        sun.markRemoved();
        addSun(sun.value());
        return sun.value();
    }

    public boolean isOver() {
        return over;
    }

    public void markOver() {
        over = true;
    }

    /** 胜负规则归属世界：所有波次生成完且场上没有存活僵尸。 */
    public boolean isWinConditionMet() {
        return level.allWavesSpawned() && zombies.stream().noneMatch(z -> !z.isRemoved());
    }

    /** 游戏逻辑更新：植物、僵尸、子弹、阳光、小推车各自的行为。 */
    public void update(double delta) {
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

    /** 清理所有已标记移除的对象。 */
    public void cleanup() {
        zombies.removeIf(GameObject::isRemoved);
        projectiles.removeIf(GameObject::isRemoved);
        suns.removeIf(GameObject::isRemoved);
        cars.removeIf(GameObject::isRemoved);
        lawn.grid().clearRemoved();
    }

}
