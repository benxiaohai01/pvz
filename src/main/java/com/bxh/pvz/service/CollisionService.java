package com.bxh.pvz.service;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.event.DeathCause;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameOverEvent;
import com.bxh.pvz.event.GameResult;
import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.PeaProjectile;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;

import java.util.Optional;

/**
 * 碰撞服务：子弹命中、僵尸啃植物、小推车收割、防线失守判定。
 */
public final class CollisionService {

    private final EventBus eventBus;

    public CollisionService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void update(GameWorld world, CombatService combat, double delta) {
        for (Zombie zombie : world.zombies()) {
            if (zombie.isRemoved()) {
                continue;
            }
            handleBite(world, combat, zombie, delta);
            handleLawnCarTrigger(world, zombie);
            handleLoseCheck(world, zombie);
        }
        handleProjectileHits(world, combat);
        handleLawnCarSweep(world, combat);
    }

    private void handleBite(GameWorld world, CombatService combat, Zombie zombie, double delta) {
        Optional<Plant> front = frontPlant(world, zombie);
        if (front.isEmpty()) {
            zombie.resetBiteTimer();
            return;
        }
        Plant plant = front.get();
        while (zombie.consumeBite(delta)) {
            combat.damage(plant, zombie.damage(), zombie);
            if (plant.isRemoved()) {
                break;
            }
        }
    }

    private void handleLawnCarTrigger(GameWorld world, Zombie zombie) {
        if (zombie.x() > GameConfig.CAR_TRIGGER_X) {
            return;
        }
        world.cars().stream()
                .filter(car -> car.row() == zombie.row() && !car.isTriggered())
                .findFirst()
                .ifPresent(LawnCar::trigger);
    }

    private void handleLoseCheck(GameWorld world, Zombie zombie) {
        if (zombie.x() > GameConfig.HOUSE_X || world.isOver()) {
            return;
        }
        world.markOver();
        eventBus.publish(new GameOverEvent(GameResult.LOSE));
    }

    private void handleProjectileHits(GameWorld world, CombatService combat) {
        for (Projectile projectile : world.projectiles()) {
            if (projectile.isRemoved()) {
                continue;
            }
            switch (projectile) {
                case PeaProjectile pea -> {
                    for (Zombie zombie : world.zombies()) {
                        if (zombie.isRemoved() || zombie.row() != pea.row()) {
                            continue;
                        }
                        if (Math.abs(pea.x() - zombie.x())
                                <= GameConfig.PEA_RADIUS + GameConfig.ZOMBIE_HALF_WIDTH) {
                            combat.damage(zombie, pea.damage(), pea);
                            pea.markRemoved();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void handleLawnCarSweep(GameWorld world, CombatService combat) {
        for (LawnCar car : world.cars()) {
            if (!car.isTriggered() || car.isRemoved()) {
                continue;
            }
            for (Zombie zombie : world.zombies()) {
                if (zombie.isRemoved() || zombie.row() != car.row()) {
                    continue;
                }
                if (Math.abs(car.x() - zombie.x())
                        <= GameConfig.LAWN_CAR_HIT_RANGE + GameConfig.ZOMBIE_HALF_WIDTH) {
                    combat.kill(zombie, DeathCause.LAWN_CAR);
                }
            }
        }
    }

    private static Optional<Plant> frontPlant(GameWorld world, Zombie zombie) {
        return world.plantsInRow(zombie.row()).stream()
                .filter(p -> !p.isRemoved())
                .filter(p -> Math.abs(p.x() - zombie.x())
                        <= GameConfig.PLANT_HALF_WIDTH + GameConfig.ZOMBIE_HALF_WIDTH)
                .findFirst();
    }
}
