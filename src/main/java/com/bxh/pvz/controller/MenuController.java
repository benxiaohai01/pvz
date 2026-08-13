package com.bxh.pvz.controller;

import com.bxh.pvz.state.GameState;
import com.bxh.pvz.state.GameStateManager;
import javafx.application.Platform;

/**
 * 主菜单控制器：只负责把用户输入映射为状态迁移。
 */
public final class MenuController {

    private final GameStateManager stateManager;

    public MenuController(GameStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void startGame() {
        stateManager.transitionTo(GameState.LEVEL_SELECT);
    }

    public void exit() {
        Platform.exit();
    }
}
