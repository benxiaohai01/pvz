package com.bxh.pvz.model.world;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.ZombieType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameWorldTest {

    @Test
    void initialWorldHasExpectedSunAndCars() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));

        assertEquals(150, world.sun());
        assertEquals(GameConfig.GRID_ROWS, world.cars().size());
        assertEquals(GameConfig.GRID_ROWS, world.lawn().rows());
        assertEquals(GameConfig.GRID_COLS, world.lawn().cols());
        assertFalse(world.isOver());
    }

    @Test
    void plantPlacementOccupiesCell() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        PlantFactory factory = TestFixture.plantFactory();

        assertTrue(world.canPlant(0, 0, TestFixture.plants().of(PlantType.SUNFLOWER).cost()));
        assertTrue(world.placePlant(factory.create(PlantType.SUNFLOWER, 0, 0)));
        assertTrue(world.lawn().grid().isOccupied(0, 0));
        assertFalse(world.canPlant(0, 0, TestFixture.plants().of(PlantType.PEASHOOTER).cost()));
    }

    @Test
    void cannotAffordExpensivePlant() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        world.spendSun(140);
        assertEquals(10, world.sun());
        assertFalse(world.canPlant(0, 0, TestFixture.plants().of(PlantType.PEASHOOTER).cost()));
    }

    @Test
    void sunCanBeFoundAndCollected() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Sun sun = new Sun(400, 300, TestFixture.plants().of(PlantType.SUNFLOWER).sunAmount());
        world.addSun(sun);

        Sun found = world.findSunAt(405, 302);
        assertNotNull(found);

        int collected = world.collectSun(found);
        assertEquals(sun.value(), collected);
        assertEquals(150 + sun.value(), world.sun());
        assertNull(world.findSunAt(405, 302));
    }

    @Test
    void zombieCanBePlacedAtRow() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        var zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 2);
        zombie.placeAtRow(world.lawn().rowCenterY(2));
        world.addZombie(zombie);

        assertEquals(1, world.zombies().size());
        assertEquals(world.lawn().rowCenterY(2), zombie.y(), 0.001);
    }

    @Test
    void entityListsAreReadOnlyViews() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));

        assertThrows(UnsupportedOperationException.class,
                () -> world.zombies().add(TestFixture.zombieFactory().create(ZombieType.BASIC, 0)));
        assertThrows(UnsupportedOperationException.class,
                () -> world.suns().add(new Sun(0, 0, TestFixture.plants().of(PlantType.SUNFLOWER).sunAmount())));
        assertThrows(UnsupportedOperationException.class,
                () -> world.cars().clear());
    }

    @Test
    void winConditionRequiresAllWavesSpawnedAndNoZombies() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        assertFalse(world.isWinConditionMet());

        var level = world.level();
        for (int i = 0; i < 100 && !level.allWavesSpawned(); i++) {
            level.tickSpawn(100);
        }
        assertTrue(world.isWinConditionMet());

        world.addZombie(TestFixture.zombieFactory().create(ZombieType.BASIC, 0));
        assertFalse(world.isWinConditionMet());
    }
}
