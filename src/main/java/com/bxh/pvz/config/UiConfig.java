package com.bxh.pvz.config;

/**
 * Window and top-level UI measurements. Gameplay layout lives in GameConfig.
 */
public final class UiConfig {

    private UiConfig() {
    }

    public static final String TITLE = "植物大战僵尸 · Java版";
    public static final double WINDOW_WIDTH = 960;
    public static final double WINDOW_HEIGHT = 640;
    public static final double UI_HEIGHT = 110;
    public static final double CANVAS_WIDTH = WINDOW_WIDTH;
    public static final double CANVAS_HEIGHT = WINDOW_HEIGHT - UI_HEIGHT;
    public static final double CARD_IMAGE_HEIGHT = 64;
}
