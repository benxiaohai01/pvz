package com.pvz.renderer;

import com.pvz.model.entity.plant.Plant;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 植物渲染：优先使用图片帧，缺失时退回颜色方块造型。
 */
public final class PlantRenderer {

    public void draw(GraphicsContext gc, Plant plant, double elapsed) {
        Image frame = SpriteCatalog.frameOf(plant.config().type(), elapsed);
        if (frame != null && !frame.isError()) {
            double width = frame.getWidth();
            double height = frame.getHeight();
            gc.drawImage(frame, plant.x() - width / 2, plant.y() - height / 2, width, height);
            return;
        }

        double width = 54;
        double height = 62;
        double x = plant.x() - width / 2;
        double y = plant.y() - height / 2;
        Color base = RendererColors.of(plant.config().color());

        switch (plant.config().type()) {
            case SUNFLOWER -> {
                gc.setFill(base);
                gc.fillRoundRect(x, y, width, height, 12, 12);
                gc.setFill(Color.web("#8B6914"));
                gc.fillOval(x + width * 0.34, y + height * 0.28, width * 0.32, height * 0.32);
            }
            case PEASHOOTER -> {
                gc.setFill(base);
                gc.fillRoundRect(x, y, width, height, 12, 12);
                gc.setFill(Color.web("#1E7B2C"));
                gc.fillRoundRect(plant.x() + 4, y + height * 0.30, 22, 12, 4, 4);
            }
            case WALLNUT -> {
                gc.setFill(base);
                gc.fillRoundRect(x, y, width, height, 18, 18);
                gc.setFill(Color.web("#6B3F1D"));
                gc.fillOval(x + width * 0.30, y + height * 0.35, 9, 9);
                gc.fillOval(x + width * 0.55, y + height * 0.35, 9, 9);
            }
        }
    }
}
