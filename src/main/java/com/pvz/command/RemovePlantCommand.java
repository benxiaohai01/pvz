package com.pvz.command;

import com.pvz.event.EventBus;
import com.pvz.event.PlantRemovalCause;
import com.pvz.event.PlantRemovedEvent;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.world.GameWorld;

/**
 * 铲除命令：移除指定格子的植物并发布事件。
 */
public final class RemovePlantCommand implements GameCommand {

    private final GameWorld world;
    private final EventBus eventBus;
    private final Plant plant;
    private boolean executed;

    public RemovePlantCommand(GameWorld world, EventBus eventBus, Plant plant) {
        this.world = world;
        this.eventBus = eventBus;
        this.plant = plant;
    }

    @Override
    public boolean canExecute() {
        return plant != null && !plant.isRemoved();
    }

    @Override
    public boolean execute() {
        if (!canExecute()) {
            return false;
        }
        world.removePlant(plant);
        eventBus.publish(new PlantRemovedEvent(plant, PlantRemovalCause.SHOVEL));
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        if (executed && !world.lawn().grid().isOccupied(plant.row(), plant.col())) {
            plant.restore();
            world.placePlant(plant);
        }
    }
}
