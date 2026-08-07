package com.pvz.model.world;

import com.pvz.config.GameConfig;
import com.pvz.config.LevelCatalog;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.entity.zombie.ZombieType;
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
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));

        assertEquals(150, world.sun());
        assertEquals(GameConfig.GRID_ROWS, world.cars().size());
        assertEquals(GameConfig.GRID_ROWS, world.lawn().rows());
        assertEquals(GameConfig.GRID_COLS, world.lawn().cols());
        assertFalse(world.isOver());
    }

    @Test
    void plantPlacementOccupiesCell() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        PlantFactory factory = new PlantFactory();

        assertTrue(world.canPlant(PlantType.SUNFLOWER, 0, 0));
        assertTrue(world.placePlant(factory.create(PlantType.SUNFLOWER, 0, 0)));
        assertTrue(world.lawn().grid().isOccupied(0, 0));
        assertFalse(world.canPlant(PlantType.PEASHOOTER, 0, 0));
    }

    @Test
    void cannotAffordExpensivePlant() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        world.spendSun(140);
        assertEquals(10, world.sun());
        assertFalse(world.canPlant(PlantType.PEASHOOTER, 0, 0));
    }

    @Test
    void sunCanBeFoundAndCollected() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        Sun sun = new Sun(400, 300, GameConfig.SUN_VALUE);
        world.addSun(sun);

        Sun found = world.findSunAt(405, 302);
        assertNotNull(found);

        int collected = world.collectSun(found);
        assertEquals(GameConfig.SUN_VALUE, collected);
        assertEquals(150 + GameConfig.SUN_VALUE, world.sun());
        assertNull(world.findSunAt(405, 302));
    }

    @Test
    void zombieCanBePlacedAtRow() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        var zombie = new ZombieFactory().create(ZombieType.BASIC, 2);
        zombie.placeAtRow(world.lawn().rowCenterY(2));
        world.addZombie(zombie);

        assertEquals(1, world.zombies().size());
        assertEquals(world.lawn().rowCenterY(2), zombie.y(), 0.001);
    }

    @Test
    void entityListsAreReadOnlyViews() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));

        assertThrows(UnsupportedOperationException.class,
                () -> world.zombies().add(new ZombieFactory().create(ZombieType.BASIC, 0)));
        assertThrows(UnsupportedOperationException.class,
                () -> world.suns().add(new Sun(0, 0, GameConfig.SUN_VALUE)));
        assertThrows(UnsupportedOperationException.class,
                () -> world.cars().clear());
    }

    @Test
    void winConditionRequiresAllWavesSpawnedAndNoZombies() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        assertFalse(world.isWinConditionMet());

        var level = world.level();
        for (int i = 0; i < level.totalWaves(); i++) {
            level.advance(100);
            level.completeWave();
        }
        assertTrue(world.isWinConditionMet());

        world.addZombie(new ZombieFactory().create(ZombieType.BASIC, 0));
        assertFalse(world.isWinConditionMet());
    }
}
