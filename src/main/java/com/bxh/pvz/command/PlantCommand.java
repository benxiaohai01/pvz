package com.bxh.pvz.command;

import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 种植命令：检查阳光/格子，执行种植，可撤销（返还阳光）。
 */
public final class PlantCommand implements GameCommand {

    private final GameWorld world;
    private final PlantFactory factory;
    private final PlantType type;
    private final int row;
    private final int col;
    private final int cost;
    private Plant placed;

    public PlantCommand(GameWorld world, PlantFactory factory, PlantType type, int row, int col, int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("cost 不能为负数: " + cost);
        }
        this.world = world;
        this.factory = factory;
        this.type = type;
        this.row = row;
        this.col = col;
        this.cost = cost;
    }

    @Override
    public boolean canExecute() {
        return world.canPlant(row, col, cost);
    }

    @Override
    public boolean execute() {
        if (!canExecute()) {
            return false;
        }
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
            world.addSun(cost);
        }
    }
}
