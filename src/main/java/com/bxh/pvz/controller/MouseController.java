package com.bxh.pvz.controller;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.PlantType;

/**
 * 鼠标控制器：把画布坐标换算为游戏动作（收集阳光、铲除植物、放置拖拽植物）。
 * 只做坐标换算，不修改游戏规则。
 */
public final class MouseController {

    private final GameController gameController;

    public MouseController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * 处理画布点击：优先收集阳光，铲子模式下再尝试铲除植物。
     */
    public void onCanvasClicked(double canvasX, double canvasY) {
        if (gameController.collectSunAt(canvasX, canvasY)) {
            return;
        }

        if (!gameController.shovelMode()) {
            return;
        }

        int column = columnAt(canvasX);
        int row = rowAt(canvasY);
        if (gameController.isCellInBounds(row, column)) {
            gameController.removePlantAt(row, column);
        }
    }

    /**
     * 处理从卡片拖到画布后的松手操作，把画布坐标换算为草坪网格。
     */
    public boolean onCanvasDropped(PlantType plantType, double canvasX, double canvasY) {
        int column = columnAt(canvasX);
        int row = rowAt(canvasY);
        if (!gameController.isCellInBounds(row, column)) {
            return false;
        }
        return gameController.placePlantAt(plantType, row, column);
    }

    /**
     * 根据画布横坐标计算草坪列号。
     */
    private int columnAt(double canvasX) {
        return (int) Math.floor((canvasX - GameConfig.GRID_X) / GameConfig.CELL_SIZE);
    }

    /**
     * 根据画布纵坐标计算草坪行号。
     */
    private int rowAt(double canvasY) {
        return (int) Math.floor((canvasY - GameConfig.GRID_Y) / GameConfig.CELL_SIZE);
    }
}
