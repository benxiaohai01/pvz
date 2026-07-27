package org.bxh.pvz.core;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.input.InputManager.PlantCard;
import org.bxh.pvz.world.GridMap;

import java.util.List;

/**
 * Canvas 2D 渲染器。持有 GraphicsContext，提供网格层、实体形状、顶部物品栏卡片
 * 和拖拽幽灵的绘制基元。
 * <p>
 * 后续替换精灵图：将各 draw* 方法的 fillRect/fillOval 改为 drawImage(texture)。
 */
public final class GameRenderer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameConfig config;

    public GameRenderer(Canvas canvas, GameConfig config) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.config = config;
    }

    /** 清空整张画布 */
    public void clear() {
        gc.clearRect(0, 0, config.windowWidth(), config.windowHeight());
    }

    // ======================== 草坪网格 ========================

    /** 绘制草坪背景和网格叠加层 */
    public void drawGrid(GridMap gridMap) {
        int ox = gridMap.offsetX();
        int oy = gridMap.offsetY();
        int cs = gridMap.cellSize();
        int rows = gridMap.rows();
        int cols = gridMap.cols();

        // 草坪背景
        gc.setFill(Color.web("#4a7c3f"));
        gc.fillRect(ox - 4, oy - 4, cols * cs + 8, rows * cs + 8);

        // 交替格子颜色
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean light = (r + c) % 2 == 0;
                gc.setFill(light ? Color.web("#5a8f4a") : Color.web("#4e7d3c"));
                gc.fillRect(ox + c * cs, oy + r * cs, cs, cs);
            }
        }

        // 网格线
        gc.setStroke(Color.web("#3a5c2f"));
        gc.setLineWidth(1);
        for (int r = 0; r <= rows; r++) {
            gc.strokeLine(ox, oy + r * cs, ox + cols * cs, oy + r * cs);
        }
        for (int c = 0; c <= cols; c++) {
            gc.strokeLine(ox + c * cs, oy, ox + c * cs, oy + rows * cs);
        }
    }

    // ======================== 顶部物品栏 ========================

    /** 绘制顶部物品栏背景 */
    public void drawTopBar() {
        gc.setFill(Color.web("#2a3a1f"));
        gc.fillRect(0, 0, config.windowWidth(), config.topBarHeight());

        // 底部分隔线
        gc.setStroke(Color.web("#5a6b4a"));
        gc.setLineWidth(2);
        gc.strokeLine(0, config.topBarHeight(), config.windowWidth(), config.topBarHeight());

        // 标题
        gc.setFill(Color.web("#c0caa0"));
        gc.setFont(Font.font("SansSerif", 13));
        gc.fillText("选择植物并拖拽到草坪种植", 150, 32);
    }

    /** 绘制植物选择卡片 */
    public void drawPlantCards(List<PlantCard> cards) {
        for (var card : cards) {
            // 卡片背景
            gc.setFill(Color.web("#5a6b4a"));
            gc.fillRoundRect(card.x(), card.y(), card.w(), card.h(), 6, 6);
            gc.setStroke(Color.web("#8a9b6a"));
            gc.setLineWidth(1);
            gc.strokeRoundRect(card.x(), card.y(), card.w(), card.h(), 6, 6);

            // 植物图标（色块占位 —— 后续替换为纹理）
            String iconColor = switch (card.plantType()) {
                case "peashooter" -> "#4CAF50";
                default -> "#888888";
            };
            gc.setFill(Color.web(iconColor));
            gc.fillRect(card.x() + 10, card.y() + 10, 18, 34);

            // 文字标签
            gc.setFill(Color.web("#c0caa0"));
            gc.setFont(Font.font("SansSerif", 12));
            gc.fillText(card.label(), card.x() + 36, card.y() + 40);
        }
    }

    // ======================== 拖拽幽灵预览 ========================

    public void drawDragGhost(String plantType, double mx, double my) {
        double w = 24, h = 44;
        String color = switch (plantType) {
            case "peashooter" -> "#4CAF50";
            default -> "#888888";
        };

        gc.setGlobalAlpha(0.5);
        gc.setFill(Color.web(color));
        gc.fillRect(mx - w / 2, my - h / 2, w, h);
        gc.setGlobalAlpha(1.0);
    }

    // ======================== 实体绘制基元 ========================

    /**
     * 绘制圆形 —— 后续替换：gc.drawImage(textureManager.get(key), cx - r, cy - r, r*2, r*2)
     */
    public void drawCircle(double cx, double cy, double radius, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    /**
     * 绘制矩形 —— 后续替换：gc.drawImage(textureManager.get(key), x, y, w, h)
     */
    public void drawRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillRect(x, y, w, h);
    }

    /**
     * 绘制圆角矩形 —— 后续替换：gc.drawImage(textureManager.get(key), x, y, w, h)
     */
    public void drawRoundedRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillRoundRect(x, y, w, h, 8, 8);
    }
}