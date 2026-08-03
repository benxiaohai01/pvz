package com.pvz.controller;

import com.pvz.core.GameState;
import com.pvz.core.GameStateManager;
import com.pvz.service.LevelService;

/**
 * 关卡选择控制器：选择关卡并进入植物选择。
 */
public final class LevelSelectController {

    private final GameStateManager stateManager;
    private final LevelService levelService;

    public LevelSelectController(GameStateManager stateManager, LevelService levelService) {
        this.stateManager = stateManager;
        this.levelService = levelService;
    }

    public void selectLevel(String levelId) {
        levelService.selectLevel(levelId);
        stateManager.transitionTo(GameState.PLANT_SELECT);
    }

    public void backToMenu() {
        stateManager.transitionTo(GameState.MENU);
    }
}
