package com.pvz.core;

/**
 * 游戏状态（State Pattern 中的状态集合），label 为中文名称。
 */
public enum GameState {
    MENU("主菜单"),
    LEVEL_SELECT("选择关卡"),
    PLANT_SELECT("选择植物"),
    PLAYING("游戏中"),
    WIN("胜利"),
    LOSE("失败");

    private final String label;

    GameState(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
