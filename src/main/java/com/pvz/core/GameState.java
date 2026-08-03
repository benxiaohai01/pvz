package com.pvz.core;

/**
 * 游戏状态（State Pattern 中的状态集合）。
 */
public enum GameState {
    MENU,
    LEVEL_SELECT,
    PLANT_SELECT,
    PLAYING,
    WIN,
    LOSE
}
