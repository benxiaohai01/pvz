package org.bxh.pvz.world;

import org.bxh.pvz.config.GameConfig;

public final class GridMap {
    private final int rows, cols, cellSize, offsetX, offsetY;
    public GridMap(GameConfig config) {
        this.rows = config.gridRows(); this.cols = config.gridCols();
        this.cellSize = config.cellSize();
        this.offsetX = config.gridOffsetX(); this.offsetY = config.gridOffsetY();
    }
    public record GridCell(int row, int col) {}
    public GridCell screenToGrid(double screenX, double screenY) {
        int col = (int) ((screenX - offsetX) / cellSize);
        int row = (int) ((screenY - offsetY) / cellSize);
        // 边界容错：边缘点击纳入最近有效格子
        if (col < 0) col = 0; else if (col >= cols) col = cols - 1;
        if (row < 0) row = 0; else if (row >= rows) row = rows - 1;
        return new GridCell(row, col);
    }
    public double cellToScreenX(int col) { return offsetX + col * cellSize + cellSize / 2.0; }
    public double cellToScreenY(int row) { return offsetY + row * cellSize + cellSize / 2.0; }
    public int rows() { return rows; } public int cols() { return cols; }
    public int cellSize() { return cellSize; }
    public int offsetX() { return offsetX; } public int offsetY() { return offsetY; }
}