package com.bxh.pvz.config;

/**
 * 游戏规则数值与棋盘坐标，独立于窗口和界面尺寸。
 */
public final class GameConfig {

    private GameConfig() {
    }

    /** 草坪行数。 */
    public static final int GRID_ROWS = 5;
    /** 草坪列数。 */
    public static final int GRID_COLS = 9;
    /** 每个草坪格子的边长。 */
    public static final double CELL_SIZE = 80;
    /** 草坪左上角在画布中的横坐标。 */
    public static final double GRID_X = 150;
    /** 草坪左上角在画布中的纵坐标。 */
    public static final double GRID_Y = 30;

    /** 僵尸攻破防线到达的最左边界。 */
    public static final double HOUSE_X = 62;
    /** 小推车初始位置。 */
    public static final double CAR_X = 86;
    /** 僵尸进入该区域后触发小推车。 */
    public static final double CAR_TRIGGER_X = 118;
    /** 僵尸出生位置相对最右草坪列的偏移，当前位于可视视口的最右侧边缘。 */
    public static final double ZOMBIE_SPAWN_MARGIN = 90;
    /** 僵尸出生位置（可视视口最右侧，随后向左进入草坪）。 */
    public static final double SPAWN_X = GRID_X + GRID_COLS * CELL_SIZE + ZOMBIE_SPAWN_MARGIN;

    /** 最多可带入关卡的植物种类数。 */
    public static final int MAX_SELECTED_PLANTS = 5;

    /** 植物占用范围的一半宽度。 */
    public static final double PLANT_HALF_WIDTH = 30;
    /** 僵尸占用范围的一半宽度。 */
    public static final double ZOMBIE_HALF_WIDTH = 18;

    /** 阳光下落速度。 */
    public static final double SUN_FALL_SPEED = 45;
    /** 阳光落地后可停留的秒数。 */
    public static final double SUN_GROUND_TTL = 8;
    /** 阳光碰撞半径。 */
    public static final double SUN_RADIUS = 14;
    /** 豌豆子弹碰撞半径。 */
    public static final double PEA_RADIUS = 6;
    /** 小推车触发后的行驶速度。 */
    public static final double LAWN_CAR_SPEED = 170;
    /** 小推车与僵尸的命中判定范围。 */
    public static final double LAWN_CAR_HIT_RANGE = 22;
    /** 子弹/小推车离开草坪右侧多远后清理。 */
    public static final double PROJECTILE_OFFSCREEN_MARGIN = 60;
    /** 小推车离开草坪右侧多远后回收。 */
    public static final double LAWN_CAR_OFFSCREEN_MARGIN = 60;
    /** 向日葵产出的阳光相对植物的偏移。 */
    public static final double SUN_SPAWN_OFFSET_Y = -18;
    /** 点击收集阳光时的容差。 */
    public static final double SUN_COLLECT_PADDING = 6;
}
