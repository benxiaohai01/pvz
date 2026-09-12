package com.bxh.pvz.renderer;

import com.bxh.pvz.config.GameConfig;
import javafx.scene.canvas.Canvas;
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

    /** 按图片原始尺寸绘制整张背景，加载失败时回退到棋盘格与房屋区域。 */
    public void draw(GraphicsContext graphicsContext) {
        Canvas canvas = graphicsContext.getCanvas();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        boolean backgroundDrawn = false;

        Image background = sprites.daytimeBackground();
        if (background != null && !background.isError()) {
            backgroundDrawn = true;
            double backgroundWidth = background.getWidth();
            double backgroundHeight = background.getHeight();
            // 不做目标宽高缩放，保留图片原始比例；超出视口的部分由横向裁切隐藏。
            graphicsContext.drawImage(background, 0, 0, backgroundWidth, backgroundHeight);

            // 仅当素材尺寸小于画布时才补色，避免窗口底部或右侧露出未绘制区域。
            graphicsContext.setFill(Color.web("#4A7C3F"));
            if (canvasHeight > backgroundHeight) {
                graphicsContext.fillRect(
                    0,
                    backgroundHeight,
                    canvasWidth,
                    canvasHeight - backgroundHeight);
            }
            if (canvasWidth > backgroundWidth) {
                graphicsContext.fillRect(
                    backgroundWidth,
                    0,
                    canvasWidth - backgroundWidth,
                    Math.min(canvasHeight, backgroundHeight));
            }
//            return;
        }

        double lawnRight = GameConfig.GRID_X + GameConfig.GRID_COLS * GameConfig.CELL_WIDTH;

        // 没有背景图时，才铺底色 + 房屋 + 右侧草地
        if (!backgroundDrawn) {
            graphicsContext.setFill(Color.web("#4A7C3F"));
            graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        }

        // 棋盘格：有背景图时只画“半透明调试层”，没有背景图时画实色格子
        for (int row = 0; row < GameConfig.GRID_ROWS; row++) {
            for (int col = 0; col < GameConfig.GRID_COLS; col++) {
                double x = GameConfig.GRID_X + col * GameConfig.CELL_WIDTH;
                double y = GameConfig.GRID_Y + row * GameConfig.CELL_HEIGHT;
//                double size = GameConfig.CELL_SIZE;

                if (!backgroundDrawn) {
                    boolean useLightTileColor = (row + col) % 2 == 0;
                    graphicsContext.setFill(useLightTileColor
                        ? Color.web("#6DB34F")
                        : Color.web("#5DA344"));
                    graphicsContext.fillRect(x, y, GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT);
                }

                // 每个格子的边框，颜色带透明度
                graphicsContext.setStroke(Color.rgb(0, 0, 0)); // 黑色
                graphicsContext.setLineWidth(1);
                graphicsContext.strokeRect(x, y, GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT);
            }
        }

        // 没有背景图时才画房屋/草地色块
        if (!backgroundDrawn) {
            graphicsContext.setFill(Color.web("#7A5B3A"));
            graphicsContext.fillRect(0, 0, GameConfig.GRID_X, canvasHeight);

            graphicsContext.setFill(Color.web("#6B8E4F"));
            graphicsContext.fillRect(lawnRight, 0, canvasWidth - lawnRight, canvasHeight);
        }

    }
}
