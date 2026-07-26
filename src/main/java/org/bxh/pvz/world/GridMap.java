package org.bxh.pvz.world;

import org.bxh.pvz.config.GameConfig;

/**
 * 网格地图 —— 草坪网格坐标系统。
 * 负责屏幕坐标 ↔ 网格坐标的转换。
 */
public final class GridMap {

    private final int rows;
    private final int cols;
    private final int cellSize;
    private final int offsetX;
    private final int offsetY;

    public GridMap(GameConfig config) {
        this.rows = config.gridRows();
        this.cols = config.gridCols();
        this.cellSize = config.cellSize();
        this.offsetX = config.gridOffsetX();
        this.offsetY = config.gridOffsetY();
    }

    public record GridCell(int row, int col) {
        public boolean isValid(int rows, int cols) {
            return row >= 0 && row < rows && col >= 0 && col < cols;
        }
    }

    /** 屏幕坐标 → 网格坐标（点击检测） */
    public GridCell screenToGrid(double screenX, double screenY) {
        int col = (int) ((screenX - offsetX) / cellSize);
        int row = (int) ((screenY - offsetY) / cellSize);
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
        return new GridCell(row, col);
    }

    /** 网格列 → 屏幕 X（格子中心） */
    public double cellToScreenX(int col) {
        return offsetX + col * cellSize + cellSize / 2.0;
    }

    /** 网格行 → 屏幕 Y（格子中心） */
    public double cellToScreenY(int row) {
        return offsetY + row * cellSize + cellSize / 2.0;
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
    public int cellSize() { return cellSize; }
    public int offsetX() { return offsetX; }
    public int offsetY() { return offsetY; }
}
