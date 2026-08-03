package com.pvz.controller;

import com.pvz.config.GameConfig;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.world.GameWorld;

/**
 * 鼠标控制器：把画布坐标翻译为游戏动作（收集阳光 / 点击格子）。
 * 只做坐标换算，不修改游戏规则。
 */
public final class MouseController {

    private final GameController gameController;

    public MouseController(GameController gameController) {
        this.gameController = gameController;
    }

    public void onCanvasClicked(double x, double y) {
        GameWorld world = gameController.world();

        Sun sun = world.findSunAt(x, y);
        if (sun != null) {
            gameController.collectSun(sun);
            return;
        }

        int col = (int) Math.floor((x - GameConfig.GRID_X) / GameConfig.CELL_SIZE);
        int row = (int) Math.floor((y - GameConfig.GRID_Y) / GameConfig.CELL_SIZE);
        if (world.lawn().grid().inBounds(row, col)) {
            gameController.onCellClicked(row, col);
        }
    }
}
