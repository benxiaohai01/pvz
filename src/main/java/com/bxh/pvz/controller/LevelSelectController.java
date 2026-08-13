package com.bxh.pvz.controller;

import com.bxh.pvz.state.GameState;
import com.bxh.pvz.state.GameStateManager;
import com.bxh.pvz.service.LevelService;

import java.util.List;

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

    public List<LevelOption> levelOptions() {
        return levelService.levels().stream()
                .map(LevelOption::from)
                .toList();
    }

    public void backToMenu() {
        stateManager.transitionTo(GameState.MENU);
    }
}
