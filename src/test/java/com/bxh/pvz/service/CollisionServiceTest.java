package com.bxh.pvz.service;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.GameOverEvent;
import com.bxh.pvz.event.GameResult;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.model.entity.projectile.PeaProjectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionServiceTest {

    @Test
    void peaHitsZombieInSameRow() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Zombie zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 0);
        zombie.placeAtRow(world.lawn().rowCenterY(0));
        world.addZombie(zombie);

        world.addProjectile(new PeaProjectile(zombie.x() - 15, zombie.y(), 0, 20, 10));
        CombatService combat = new CombatService(new EventBus());
        CollisionService collision = new CollisionService(new EventBus());

        collision.update(world, combat, 0.016);
        assertEquals(80, zombie.hp());
        world.cleanup();
        assertTrue(world.projectiles().isEmpty());
    }

    @Test
    void zombieBitesPlantUntilDeath() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Plant plant = TestFixture.plantFactory().create(PlantType.SUNFLOWER, 0, 0);
        world.placePlant(plant);

        Zombie zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 0);
        zombie.placeAtRow(world.lawn().rowCenterY(0));
        zombie.setPosition(plant.x(), plant.y());
        world.addZombie(zombie);

        CombatService combat = new CombatService(new EventBus());
        CollisionService collision = new CollisionService(new EventBus());
        for (int i = 0; i < 10; i++) {
            collision.update(world, combat, 1.0);
        }

        world.cleanup();
        assertTrue(plant.isRemoved());
        assertTrue(world.plants().stream().noneMatch(p -> p.row() == 0 && p.col() == 0));
    }

    @Test
    void zombieReachingHouseTriggersLose() {
        EventBus eventBus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        eventBus.subscribe(events::add);

        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Zombie zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 0);
        zombie.placeAtRow(world.lawn().rowCenterY(0));
        world.addZombie(zombie);

        CombatService combat = new CombatService(eventBus);
        CollisionService collision = new CollisionService(eventBus);

        zombie.setPosition(GameConfig.HOUSE_X - 10, zombie.y());
        collision.update(world, combat, 0.016);

        assertTrue(world.isOver());
        GameOverEvent over = (GameOverEvent) events.stream()
                .filter(e -> e instanceof GameOverEvent)
                .findFirst().orElseThrow();
        assertEquals(GameResult.LOSE, over.result());
    }
}
