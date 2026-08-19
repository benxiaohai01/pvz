package com.bxh.pvz.controller;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.state.GameState;
import com.bxh.pvz.state.GameStateManager;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.service.LevelService;

import java.util.ArrayList;
import java.util.List;

/**
 * 植物选择控制器：管理已选植物（最多 5 种）并启动游戏。
 */
public final class PlantSelectController {

    private final GameStateManager stateManager;
    private final LevelService levelService;
    private final PlantCatalog plantCatalog;
    private final GameSessionStarter sessionStarter;
    /** 玩家在本局选中的植物类型，顺序即顶部选择栏的展示顺序。 */
    private final List<PlantType> selectedPlantTypes = new ArrayList<>();

    public PlantSelectController(
            GameStateManager stateManager,
            LevelService levelService,
            PlantCatalog plantCatalog,
            GameSessionStarter sessionStarter) {
        this.stateManager = stateManager;
        this.levelService = levelService;
        this.plantCatalog = plantCatalog;
        this.sessionStarter = sessionStarter;
    }

    public List<PlantOption> availableOptions() {
        return levelService.currentLevel().availablePlants().stream()
                .map(plantCatalog::of)
                .map(PlantOption::from)
                .toList();
    }

    public boolean isSelected(PlantType type) {
        return selectedPlantTypes.contains(type);
    }

    public boolean isFull() {
        return selectedPlantTypes.size() >= GameConfig.MAX_SELECTED_PLANTS;
    }

    public List<PlantType> selectedPlants() {
        return List.copyOf(selectedPlantTypes);
    }

    /** 点击卡片：已选则移除，未选且未满则添加。 */
    public void toggle(PlantType type) {
        if (selectedPlantTypes.contains(type)) {
            selectedPlantTypes.remove(type);
        } else if (!isFull()) {
            selectedPlantTypes.add(type);
        }
    }

    public void startGame() {
        if (!selectedPlantTypes.isEmpty()) {
            sessionStarter.startGame(levelService.currentLevel(), selectedPlants());
        }
    }

    public void backToLevelSelect() {
        stateManager.transitionTo(GameState.LEVEL_SELECT);
    }
}
