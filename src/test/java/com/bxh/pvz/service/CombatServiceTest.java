package com.bxh.pvz.service;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.event.DeathCause;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.PlantRemovalCause;
import com.bxh.pvz.event.PlantRemovedEvent;
import com.bxh.pvz.event.ZombieDeathEvent;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.model.entity.projectile.PeaProjectile;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {

    @Test
    void zombieDeathPublishesEventWithProjectileCause() {
        EventBus eventBus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        eventBus.subscribe(events::add);

        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        var zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 0);
        world.addZombie(zombie);
        CombatService combat = new CombatService(eventBus);

        combat.damage(zombie, 60, new PeaProjectile(0, 0, 0, 20, 10));
        assertEquals(40, zombie.hp());
        assertTrue(events.isEmpty());

        combat.damage(zombie, 60, new PeaProjectile(0, 0, 0, 20, 10));
        assertEquals(0, zombie.hp());
        assertTrue(zombie.isRemoved());
        assertEquals(1, events.size());

        ZombieDeathEvent death = (ZombieDeathEvent) events.get(0);
        assertEquals(zombie, death.zombie());
        assertEquals(DeathCause.PROJECTILE, death.cause());
    }

    @Test
    void plantDeathPublishesRemovedEvent() {
        EventBus eventBus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        eventBus.subscribe(events::add);

        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        var plant = TestFixture.plantFactory().create(PlantType.SUNFLOWER, 0, 0);
        world.placePlant(plant);
        CombatService combat = new CombatService(eventBus);

        combat.damage(plant, 200, plant);
        assertTrue(plant.isRemoved());

        PlantRemovedEvent removed = (PlantRemovedEvent) events.get(0);
        assertEquals(PlantRemovalCause.DEATH, removed.cause());
    }
}
