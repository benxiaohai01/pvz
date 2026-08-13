package com.bxh.pvz.renderer;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.UiConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 界面背景渲染：优先使用背景图片，缺失时退回棋盘绘制。
 */
public final class UIBackgroundRenderer {

    private final SpriteCatalog sprites;

    public UIBackgroundRenderer(SpriteCatalog sprites) {
        this.sprites = sprites;
    }

    public void draw(GraphicsContext gc) {
        Image background = sprites.daytimeBackground();
        if (background != null && !background.isError()) {
            gc.drawImage(background, 0, 0, UiConfig.CANVAS_WIDTH, UiConfig.CANVAS_HEIGHT);
            return;
        }

        double lawnRight = GameConfig.GRID_X + GameConfig.GRID_COLS * GameConfig.CELL_SIZE;

        gc.setFill(Color.web("#4A7C3F"));
        gc.fillRect(0, 0, UiConfig.CANVAS_WIDTH, UiConfig.CANVAS_HEIGHT);

        for (int row = 0; row < GameConfig.GRID_ROWS; row++) {
            for (int col = 0; col < GameConfig.GRID_COLS; col++) {
                boolean light = (row + col) % 2 == 0;
                gc.setFill(light ? Color.web("#6DB34F") : Color.web("#5DA344"));
                gc.fillRect(
                        GameConfig.GRID_X + col * GameConfig.CELL_SIZE,
                        GameConfig.GRID_Y + row * GameConfig.CELL_SIZE,
                        GameConfig.CELL_SIZE,
                        GameConfig.CELL_SIZE);
            }
        }

        gc.setFill(Color.web("#7A5B3A"));
        gc.fillRect(0, 0, GameConfig.GRID_X, UiConfig.CANVAS_HEIGHT);

        gc.setFill(Color.web("#6B8E4F"));
        gc.fillRect(lawnRight, 0, UiConfig.CANVAS_WIDTH - lawnRight, UiConfig.CANVAS_HEIGHT);
    }
}
