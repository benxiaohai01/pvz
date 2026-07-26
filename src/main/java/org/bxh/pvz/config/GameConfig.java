package org.bxh.pvz.config;

public record GameConfig(
        int windowWidth,
        int windowHeight,
        String windowTitle,
        int gridRows,
        int gridCols,
        int cellSize,
        int sidebarWidth) {

    public static GameConfig defaultConfig() {
        return new GameConfig(
                1024, 768,
                "Plants vs Zombies",
                5, 9,
                80, 160);
    }

    /** 游戏区域宽度（不含侧边栏） */
    public int gameAreaWidth() {
        return windowWidth - sidebarWidth;
    }

    /** 网格起始 X 偏移，让网格居中 */
    public int gridOffsetX() {
        return (gameAreaWidth() - gridCols * cellSize) / 2;
    }

    /** 网格起始 Y 偏移 */
    public int gridOffsetY() {
        return (windowHeight - gridRows * cellSize) / 2;
    }
}
