package org.bxh.pvz.config;

/**
 * 【设计模式：不可变配置（Immutable Configuration）—— record 保证配置不可变】
 * 游戏全局配置。顶部物品栏 + 下方草坪网格。
 */
public record GameConfig(
        int windowWidth,
        int windowHeight,
        String windowTitle,
        int gridRows,
        int gridCols,
        int cellSize,
        int topBarHeight) {

    /** 默认配置：1024x768，顶部栏90px，5行9列，格子80px */
    public static GameConfig defaultConfig() {
        return new GameConfig(1024, 768, "Plants vs Zombies", 5, 9, 80, 90);
    }

    /** 网格起始 X 偏移（水平居中） */
    public int gridOffsetX() {
        return (windowWidth - gridCols * cellSize) / 2;
    }

    /** 网格起始 Y 偏移（顶部栏下方垂直居中） */
    public int gridOffsetY() {
        int gameHeight = windowHeight - topBarHeight;
        return topBarHeight + (gameHeight - gridRows * cellSize) / 2;
    }
}