package com.bxh.pvz.renderer;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.projectile.Projectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 动画渲染：阳光浮动、豌豆、小推车。
 */
public final class AnimationRenderer {

    public void drawSun(GraphicsContext gc, Sun sun, double elapsed) {
        double bob = sun.isGrounded()
                ? Math.sin(elapsed * 2.5 + sun.x() * 0.04) * 2
                : 0;
        double cx = sun.x();
        double cy = sun.y() + bob;
        double radius = GameConfig.SUN_RADIUS;

        gc.setFill(Color.GOLD);
        gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(1.5);
        gc.strokeOval(cx - radius, cy - radius, radius * 2, radius * 2);

        gc.setStroke(Color.GOLD);
        for (int i = 0; i < 8; i++) {
            double angle = elapsed * 1.4 + i * Math.PI / 4;
            double x1 = cx + Math.cos(angle) * (radius + 1);
            double y1 = cy + Math.sin(angle) * (radius + 1);
            double x2 = cx + Math.cos(angle) * (radius + 8);
            double y2 = cy + Math.sin(angle) * (radius + 8);
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public void drawPea(GraphicsContext gc, Projectile projectile) {
        gc.setFill(Color.web("#7CFC00"));
        gc.fillOval(
                projectile.x() - GameConfig.PEA_RADIUS,
                projectile.y() - GameConfig.PEA_RADIUS,
                GameConfig.PEA_RADIUS * 2,
                GameConfig.PEA_RADIUS * 2);
    }

    public void drawLawnCar(GraphicsContext gc, LawnCar car) {
        double width = 46;
        double height = 26;
        double x = car.x() - width / 2;
        double y = car.y() - height / 2;

        gc.setFill(Color.web("#1E90FF"));
        gc.fillRoundRect(x, y, width, height, 6, 6);
        gc.setFill(Color.web("#0B5BA5"));
        gc.fillRoundRect(x, y - 7, width, 8, 3, 3);
    }
}
