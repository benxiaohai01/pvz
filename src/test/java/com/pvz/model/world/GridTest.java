package com.pvz.model.world;

import com.pvz.factory.PlantFactory;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridTest {

    private final PlantFactory factory = new PlantFactory();

    @Test
    void placeOccupiesCellAndCanBeRemoved() {
        Grid grid = new Grid(5, 9);
        Plant plant = factory.create(PlantType.SUNFLOWER, 2, 3);

        assertTrue(grid.place(plant));
        assertTrue(grid.isOccupied(2, 3));
        assertEquals(plant, grid.plantAt(2, 3));

        assertNotNull(grid.remove(2, 3));
        assertFalse(grid.isOccupied(2, 3));
        assertNull(grid.plantAt(2, 3));
    }

    @Test
    void cannotPlaceTwiceInSameCellOrOutOfBounds() {
        Grid grid = new Grid(5, 9);
        assertTrue(grid.place(factory.create(PlantType.PEASHOOTER, 0, 0)));
        assertFalse(grid.place(factory.create(PlantType.WALLNUT, 0, 0)));
        assertFalse(grid.place(factory.create(PlantType.WALLNUT, -1, 0)));
        assertFalse(grid.place(factory.create(PlantType.WALLNUT, 0, 9)));
    }

    @Test
    void clearRemovedFreesCells() {
        Grid grid = new Grid(5, 9);
        Plant plant = factory.create(PlantType.WALLNUT, 1, 1);
        grid.place(plant);
        plant.markRemoved();

        // 已死亡的植物不占用格子（等待清理）
        assertFalse(grid.isOccupied(1, 1));
        grid.clearRemoved();
        assertFalse(grid.isOccupied(1, 1));
        assertNull(grid.plantAt(1, 1));
    }

    @Test
    void plantsReturnsAllEntries() {
        Grid grid = new Grid(5, 9);
        Plant a = factory.create(PlantType.SUNFLOWER, 0, 0);
        Plant b = factory.create(PlantType.PEASHOOTER, 4, 8);
        grid.place(a);
        grid.place(b);

        assertEquals(2, grid.plants().size());
    }
}
