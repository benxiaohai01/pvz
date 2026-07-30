package org.bxh.pvz.core;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.input.InputManager.PlantCard;
import org.bxh.pvz.world.GridMap;
import java.util.List;

public final class GameRenderer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameConfig config;

    public GameRenderer(Canvas canvas, GameConfig config) {
        this.canvas = canvas; this.gc = canvas.getGraphicsContext2D(); this.config = config;
    }

    public GraphicsContext gc() { return gc; }

    public void clear() { gc.clearRect(0, 0, config.windowWidth(), config.windowHeight()); }

    public void drawTopBar() {
        gc.setFill(Color.web("#2a3a1f"));
        gc.fillRect(0, 0, config.windowWidth(), config.topBarHeight());
        gc.setStroke(Color.web("#5a6b4a")); gc.setLineWidth(2);
        gc.strokeLine(0, config.topBarHeight(), config.windowWidth(), config.topBarHeight());
    }

    public void drawGrid(GridMap gridMap) {
        int ox = gridMap.offsetX(), oy = gridMap.offsetY(), cs = gridMap.cellSize();
        gc.setFill(Color.web("#4a7c3f"));
        gc.fillRect(ox - 4, oy - 4, gridMap.cols() * cs + 8, gridMap.rows() * cs + 8);
        for (int r = 0; r < gridMap.rows(); r++)
            for (int c = 0; c < gridMap.cols(); c++) {
                gc.setFill((r + c) % 2 == 0 ? Color.web("#5a8f4a") : Color.web("#4e7d3c"));
                gc.fillRect(ox + c * cs, oy + r * cs, cs, cs);
            }
        gc.setStroke(Color.web("#3a5c2f")); gc.setLineWidth(1);
        for (int r = 0; r <= gridMap.rows(); r++) gc.strokeLine(ox, oy + r * cs, ox + gridMap.cols() * cs, oy + r * cs);
        for (int c = 0; c <= gridMap.cols(); c++) gc.strokeLine(ox + c * cs, oy, ox + c * cs, oy + gridMap.rows() * cs);
    }

    public void drawPlantCards(List<PlantCard> cards) {
        for (var card : cards) {
            gc.setFill(Color.web("#5a6b4a")); gc.fillRoundRect(card.x(), card.y(), card.w(), card.h(), 6, 6);
            gc.setStroke(Color.web("#8a9b6a")); gc.setLineWidth(1);
            gc.strokeRoundRect(card.x(), card.y(), card.w(), card.h(), 6, 6);
            String iconColor = "peashooter".equals(card.plantType()) ? "#4CAF50" : "#FFD700";
            gc.setFill(Color.web(iconColor)); gc.fillRect(card.x() + 10, card.y() + 10, 18, 34);
            gc.setFill(Color.web("#c0caa0")); gc.setFont(Font.font("SansSerif", 12));
            gc.fillText(card.label() + " (" + card.price() + ")", card.x() + 36, card.y() + 40);
        }
    }

    public void drawDragGhost(String plantType, double mx, double my) {
        double w = 24, h = 44;
        String color = "peashooter".equals(plantType) ? "#4CAF50" : "#FFD700";
        gc.setGlobalAlpha(0.5); gc.setFill(Color.web(color));
        gc.fillRect(mx - w / 2, my - h / 2, w, h); gc.setGlobalAlpha(1.0);
    }

    public void drawSunCounter(int sun) {
        gc.setFill(Color.web("#FFB74D")); gc.setFont(Font.font("SansSerif", 18));
        gc.fillText("阳光: " + sun, config.windowWidth() - 180, 35);
    }

    public void drawCircle(double cx, double cy, double radius, String colorHex) {
        gc.setFill(Color.web(colorHex)); gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }
    public void drawRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex)); gc.fillRect(x, y, w, h);
    }
    public void drawRoundedRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex)); gc.fillRoundRect(x, y, w, h, 8, 8);
    }
}