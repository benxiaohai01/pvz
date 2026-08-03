package com.pvz.command;

import com.pvz.config.PlantCatalog;
import com.pvz.factory.PlantFactory;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.world.GameWorld;

/**
 * 种植命令：检查阳光/格子，执行种植，可撤销（返还阳光）。
 */
public final class PlantCommand implements GameCommand {

    private final GameWorld world;
    private final PlantFactory factory;
    private final PlantType type;
    private final int row;
    private final int col;
    private Plant placed;

    public PlantCommand(GameWorld world, PlantFactory factory, PlantType type, int row, int col) {
        this.world = world;
        this.factory = factory;
        this.type = type;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean canExecute() {
        return world.canPlant(type, row, col);
    }

    @Override
    public boolean execute() {
        if (!canExecute()) {
            return false;
        }
        int cost = PlantCatalog.of(type).cost();
        if (!world.spendSun(cost)) {
            return false;
        }
        placed = factory.create(type, row, col);
        if (!world.placePlant(placed)) {
            world.addSun(cost);
            placed = null;
            return false;
        }
        return true;
    }

    @Override
    public void undo() {
        if (placed != null && !placed.isRemoved()) {
            world.removePlant(placed);
            world.addSun(PlantCatalog.of(type).cost());
        }
    }
}
