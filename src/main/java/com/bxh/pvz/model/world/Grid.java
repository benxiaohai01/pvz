package com.bxh.pvz.model.world;

import com.bxh.pvz.model.entity.plant.Plant;

import java.util.ArrayList;
import java.util.List;

/**
 * 草坪网格：负责植物占位与查询，是纯领域对象。
 */
public final class Grid {

    private final int rows;
    private final int cols;
    private final Plant[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Plant[rows][cols];
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isOccupied(int row, int col) {
        Plant plant = plantAt(row, col);
        return plant != null && !plant.isRemoved();
    }

    public Plant plantAt(int row, int col) {
        if (!inBounds(row, col)) {
            return null;
        }
        return cells[row][col];
    }

    public boolean place(Plant plant) {
        int row = plant.row();
        int col = plant.col();
        if (!inBounds(row, col) || isOccupied(row, col)) {
            return false;
        }
        cells[row][col] = plant;
        return true;
    }

    public Plant remove(int row, int col) {
        Plant removed = plantAt(row, col);
        if (removed != null) {
            cells[row][col] = null;
        }
        return removed;
    }

    /** 网格中仍持有引用的植物，包含已标记移除、等待帧末清理的对象。 */
    public List<Plant> plants() {
        List<Plant> result = new ArrayList<>(rows * cols);
        for (Plant[] row : cells) {
            for (Plant plant : row) {
                if (plant != null) {
                    result.add(plant);
                }
            }
        }
        return result;
    }

    /** 清掉所有已经死亡的植物格子。 */
    public void clearRemoved() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (cells[row][col] != null && cells[row][col].isRemoved()) {
                    cells[row][col] = null;
                }
            }
        }
    }
}
