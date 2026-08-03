package com.pvz.model.world;

import com.pvz.config.GameConfig;

/**
 * 草坪：5 × 9 网格 + 行/列坐标换算。
 */
public final class Lawn {

    private final Grid grid;

    public Lawn(int rows, int cols) {
        this.grid = new Grid(rows, cols);
    }

    public Grid grid() {
        return grid;
    }

    public int rows() {
        return grid.rows();
    }

    public int cols() {
        return grid.cols();
    }

    public double rowCenterY(int row) {
        return GameConfig.GRID_Y + row * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2.0;
    }

    public double colCenterX(int col) {
        return GameConfig.GRID_X + col * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2.0;
    }

    public double leftX() {
        return GameConfig.GRID_X;
    }

    public double rightX() {
        return GameConfig.GRID_X + cols() * GameConfig.CELL_SIZE;
    }
}
