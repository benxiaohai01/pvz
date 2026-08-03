package com.pvz.controller;

import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.level.LevelConfig;

import java.util.List;

/**
 * 开始游戏回调：由引擎实现，解耦植物选择界面与具体启动流程。
 */
public interface GameSessionStarter {

    void startGame(LevelConfig level, List<PlantType> selectedPlants);
}
