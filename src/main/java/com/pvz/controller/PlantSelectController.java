package com.pvz.controller;

import com.pvz.config.GameConfig;
import com.pvz.core.GameState;
import com.pvz.core.GameStateManager;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.service.LevelService;

import java.util.ArrayList;
import java.util.List;

/**
 * 植物选择控制器：管理已选植物（最多 5 种）并启动游戏。
 */
public final class PlantSelectController {

    private final GameStateManager stateManager;
    private final LevelService levelService;
    private final GameSessionStarter sessionStarter;
    private final List<PlantType> selected = new ArrayList<>();

    public PlantSelectController(
            GameStateManager stateManager,
            LevelService levelService,
            GameSessionStarter sessionStarter) {
        this.stateManager = stateManager;
        this.levelService = levelService;
        this.sessionStarter = sessionStarter;
    }

    public List<PlantType> availablePlants() {
        return levelService.currentLevel().availablePlants();
    }

    public boolean isSelected(PlantType type) {
        return selected.contains(type);
    }

    public boolean isFull() {
        return selected.size() >= GameConfig.MAX_SELECTED_PLANTS;
    }

    public List<PlantType> selectedPlants() {
        return List.copyOf(selected);
    }

    /** 点击卡片：已选则移除，未选且未满则添加。 */
    public void toggle(PlantType type) {
        if (selected.contains(type)) {
            selected.remove(type);
        } else if (!isFull()) {
            selected.add(type);
        }
    }

    public void startGame() {
        if (!selected.isEmpty()) {
            sessionStarter.startGame(levelService.currentLevel(), selectedPlants());
        }
    }

    public void backToLevelSelect() {
        stateManager.transitionTo(GameState.LEVEL_SELECT);
    }
}
