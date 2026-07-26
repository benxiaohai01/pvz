package org.bxh.pvz.core;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.world.GridMap;

/**
 * Canvas-based 2D renderer. Owns the {@link GraphicsContext} and provides
 * draw primitives for both the grid layer and entity shapes.
 * <p>
 * Phase 1 uses solid-colour geometry; later phases swap in sprites via
 * {@code TextureManager} lookups.
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

    // -- Frame lifecycle ----------------------------------------------------

    /** Clear the entire canvas before a new frame. */
    public void clear() {
        gc.clearRect(0, 0, config.windowWidth(), config.windowHeight());
    }

    // -- Grid layer ---------------------------------------------------------

    /** Draw the lawn background and grid overlay. */
    public void drawGrid(GridMap gridMap) {
        int ox = gridMap.offsetX();
        int oy = gridMap.offsetY();
        int cs = gridMap.cellSize();
        int rows = gridMap.rows();
        int cols = gridMap.cols();

        // Lawn background
        gc.setFill(Color.web("#4a7c3f"));
        gc.fillRect(ox - 4, oy - 4, cols * cs + 8, rows * cs + 8);

        // Alternating cell colours
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean light = (r + c) % 2 == 0;
                gc.setFill(light ? Color.web("#5a8f4a") : Color.web("#4e7d3c"));
                gc.fillRect(ox + c * cs, oy + r * cs, cs, cs);
            }
        }

        // Grid lines
        gc.setStroke(Color.web("#3a5c2f"));
        gc.setLineWidth(1);
        for (int r = 0; r <= rows; r++) {
            gc.strokeLine(ox, oy + r * cs, ox + cols * cs, oy + r * cs);
        }
        for (int c = 0; c <= cols; c++) {
            gc.strokeLine(ox + c * cs, oy, ox + c * cs, oy + rows * cs);
        }

        // Sidebar separator
        int sepX = config.gameAreaWidth();
        gc.setStroke(Color.web("#5a6b4a"));
        gc.setLineWidth(2);
        gc.strokeLine(sepX, 0, sepX, config.windowHeight());

        // Sidebar background
        gc.setFill(Color.web("#3a4a2f"));
        gc.fillRect(sepX, 0, config.sidebarWidth(), config.windowHeight());

        // Sidebar header
        gc.setFill(Color.web("#c0caa0"));
        gc.setFont(javafx.scene.text.Font.font("SansSerif", 14));
        gc.fillText("Plants", sepX + 50, 40);
    }

    // -- Entity shape primitives --------------------------------------------

    public void drawCircle(double cx, double cy, double radius, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    public void drawRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillRect(x, y, w, h);
    }

    public void drawRoundedRect(double x, double y, double w, double h, String colorHex) {
        gc.setFill(Color.web(colorHex));
        gc.fillRoundRect(x, y, w, h, 8, 8);
    }
}
