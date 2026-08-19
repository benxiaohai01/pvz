package com.bxh.pvz.controller;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.PlantRemovalCause;
import com.bxh.pvz.event.PlantRemovedEvent;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.model.world.GameWorld;
import com.bxh.pvz.service.CollisionService;
import com.bxh.pvz.service.CombatService;
import com.bxh.pvz.service.SpawnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖拖拽种植、铲除和阳光扣除等控制器编排逻辑。
 */
class GameControllerTest {

    private EventBus eventBus;
    private GameWorld world;
    private GameController gameController;
    private PlantCatalog plantCatalog;

    @BeforeEach
    void setUpController() {
        eventBus = new EventBus();
        world = TestFixture.world("1-1");
        plantCatalog = TestFixture.plants();
        PlantFactory plantFactory = TestFixture.plantFactory();
        gameController = new GameController(
                world,
                List.of(PlantType.SUNFLOWER, PlantType.PEASHOOTER),
                eventBus,
                plantFactory,
                plantCatalog,
                new CombatService(eventBus),
                new CollisionService(eventBus),
                new SpawnService(TestFixture.zombieFactory(), eventBus));
    }

    @Test
    void successfulDropPlacesPlantSpendsSunAndStartsCooldown() {
        int initialSun = world.sun();
        int sunflowerCost = plantCatalog.of(PlantType.SUNFLOWER).cost();

        assertTrue(gameController.placePlantAt(PlantType.SUNFLOWER, 0, 0));

        assertEquals(initialSun - sunflowerCost, world.sun());
        assertTrue(world.lawn().grid().isOccupied(0, 0));
        assertEquals(
                plantCatalog.of(PlantType.SUNFLOWER).cooldown(),
                gameController.cooldownRemaining(PlantType.SUNFLOWER),
                0.001);
        assertFalse(gameController.canStartPlantDrag(PlantType.SUNFLOWER));
    }

    @Test
    void insufficientSunRejectsPlantWithoutChangingWorld() {
        world.spendSun(world.sun());

        assertFalse(gameController.canStartPlantDrag(PlantType.PEASHOOTER));
        assertFalse(gameController.placePlantAt(PlantType.PEASHOOTER, 0, 0));

        assertEquals(0, world.sun());
        assertFalse(world.lawn().grid().isOccupied(0, 0));
        assertEquals(0.0, gameController.cooldownRemaining(PlantType.PEASHOOTER), 0.001);
    }

    @Test
    void shovelModeRemovesPlacedPlantAndPublishesEvent() {
        assertTrue(gameController.placePlantAt(PlantType.SUNFLOWER, 2, 3));
        AtomicReference<PlantRemovedEvent> removedEvent = new AtomicReference<>();
        eventBus.subscribe(event -> {
            if (event instanceof PlantRemovedEvent plantRemovedEvent) {
                removedEvent.set(plantRemovedEvent);
            }
        });

        gameController.toggleShovel();
        gameController.removePlantAt(2, 3);

        assertFalse(world.lawn().grid().isOccupied(2, 3));
        assertEquals(PlantRemovalCause.SHOVEL, removedEvent.get().cause());
    }

    @Test
    void mouseDropConvertsCanvasCoordinatesToGridCell() {
        MouseController mouseController = new MouseController(gameController);
        double firstCellCenterX = GameConfig.GRID_X + GameConfig.CELL_SIZE / 2.0;
        double firstCellCenterY = GameConfig.GRID_Y + GameConfig.CELL_SIZE / 2.0;

        assertTrue(mouseController.onCanvasDropped(PlantType.SUNFLOWER, firstCellCenterX, firstCellCenterY));
        assertTrue(world.lawn().grid().isOccupied(0, 0));
        assertFalse(mouseController.onCanvasDropped(PlantType.PEASHOOTER, -10, -10));
    }

    @Test
    void droppingOnOccupiedCellDoesNotSpendSun() {
        assertTrue(gameController.placePlantAt(PlantType.SUNFLOWER, 1, 1));
        int sunAfterFirstPlant = world.sun();

        assertFalse(gameController.placePlantAt(PlantType.PEASHOOTER, 1, 1));

        assertEquals(sunAfterFirstPlant, world.sun());
        assertNotNull(world.lawn().grid().plantAt(1, 1));
        assertEquals(PlantType.SUNFLOWER, world.lawn().grid().plantAt(1, 1).config().type());
        assertTrue(world.lawn().grid().isOccupied(1, 1));
    }
}
