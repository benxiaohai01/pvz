package com.pvz.config;

/**
 * 全局游戏常量（数据驱动配置的基础）。
 */
public final class GameConfig {

    private GameConfig() {
    }

    public static final String TITLE = "植物大战僵尸 · Java版";

    public static final double WINDOW_WIDTH = 960;
    public static final double WINDOW_HEIGHT = 640;
    public static final double UI_HEIGHT = 110;
    public static final double CANVAS_WIDTH = WINDOW_WIDTH;
    public static final double CANVAS_HEIGHT = WINDOW_HEIGHT - UI_HEIGHT;

    public static final int GRID_ROWS = 5;
    public static final int GRID_COLS = 9;
    public static final double CELL_SIZE = 80;
    public static final double GRID_X = 150;
    public static final double GRID_Y = 30;

    /** 僵尸攻破防线到达的最左边界。 */
    public static final double HOUSE_X = 62;
    /** 小推车初始位置。 */
    public static final double CAR_X = 86;
    /** 僵尸进入该区域后触发小推车。 */
    public static final double CAR_TRIGGER_X = 118;
    /** 僵尸出生位置（草坪右侧之外）。 */
    public static final double SPAWN_X = GRID_X + GRID_COLS * CELL_SIZE + 40;

    public static final int MAX_SELECTED_PLANTS = 5;

    public static final double PLANT_HALF_WIDTH = 30;
    public static final double ZOMBIE_HALF_WIDTH = 18;

    public static final int SUN_VALUE = 25;
    public static final double SUN_FALL_SPEED = 45;
    public static final double SUN_GROUND_TTL = 8;
    public static final double SUN_RADIUS = 14;
    public static final double PEA_RADIUS = 6;
    public static final double LAWN_CAR_SPEED = 170;
}
