package com.bxh.pvz.controller;

import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.LevelConfig;

import java.util.List;

/**
 * 开始游戏回调：由引擎实现，解耦植物选择界面与具体启动流程。
 */
public interface GameSessionStarter {

    void startGame(LevelConfig level, List<PlantType> selectedPlants);
}
