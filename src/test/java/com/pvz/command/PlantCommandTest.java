package com.pvz.command;

import com.pvz.config.LevelCatalog;
import com.pvz.event.EventBus;
import com.pvz.event.GameEvent;
import com.pvz.event.PlantRemovedEvent;
import com.pvz.factory.PlantFactory;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantCommandTest {

    @Test
    void plantCommandExecutesAndUndoesWithRefund() {
        GameWorld world = new GameWorld(LevelCatalog.LEVEL_1_1);
        PlantCommand command = new PlantCommand(world, new PlantFactory(), PlantType.SUNFLOWER, 0, 0);

        assertTrue(command.canExecute());
        assertTrue(command.execute());
        assertEquals(100, world.sun());
        assertTrue(world.lawn().grid().isOccupied(0, 0));

        command.undo();
        assertEquals(150, world.sun());
        assertFalse(world.lawn().grid().isOccupied(0, 0));
    }

    @Test
    void plantCommandCannotExecuteOnOccupiedCell() {
        GameWorld world = new GameWorld(LevelCatalog.LEVEL_1_1);
        PlantFactory factory = new PlantFactory();

        new PlantCommand(world, factory, PlantType.PEASHOOTER, 0, 0).execute();
        PlantCommand second = new PlantCommand(world, factory, PlantType.SUNFLOWER, 0, 0);

        assertFalse(second.canExecute());
        assertFalse(second.execute());
    }

    @Test
    void removePlantCommandPublishesEventAndCanUndo() {
        EventBus eventBus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        eventBus.subscribe(events::add);

        GameWorld world = new GameWorld(LevelCatalog.LEVEL_1_1);
        var plant = new PlantFactory().create(PlantType.PEASHOOTER, 1, 2);
        world.placePlant(plant);

        RemovePlantCommand command = new RemovePlantCommand(world, eventBus, plant);
        assertTrue(command.execute());
        assertFalse(world.lawn().grid().isOccupied(1, 2));
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof PlantRemovedEvent);

        command.undo();
        assertTrue(world.lawn().grid().isOccupied(1, 2));
    }
}
