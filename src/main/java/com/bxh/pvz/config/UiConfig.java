package com.bxh.pvz.config;

/**
 * 窗口与顶层界面尺寸配置；草坪布局尺寸位于游戏规则配置类。
 */
public final class UiConfig {

    private UiConfig() {
    }

    /** 游戏窗口标题。 */
    public static final String TITLE = "植物大战僵尸 · Java版";
    /** 游戏窗口宽度。 */
    public static final double WINDOW_WIDTH = 960;
    /** 游戏窗口高度。 */
    public static final double WINDOW_HEIGHT = 640;
    /** 顶部操作栏高度：预留卡片图片、冷却标签和上下内边距的空间。 */
    public static final double UI_HEIGHT = 104;
    /** 横向可视视口宽度：完整背景和游戏画布中只显示这一部分。 */
    public static final double CANVAS_WIDTH = WINDOW_WIDTH;
    /** 商店栏下方游戏对象画布的高度。 */
    public static final double CANVAS_HEIGHT = WINDOW_HEIGHT - UI_HEIGHT;
    /** 植物卡片中图片的目标高度。 */
    public static final double CARD_IMAGE_HEIGHT = 64;
    /** 开局镜头向右滑到道路区域所需秒数。 */
    public static final double INTRO_FORWARD_SECONDS = 0.8;
    /** 开局镜头停留在道路区域预览的秒数。 */
    public static final double INTRO_PREVIEW_SECONDS = 1.0;
    /** 开局镜头从道路区域滑回草坪所需秒数。 */
    public static final double INTRO_RETURN_SECONDS = 0.9;
}
