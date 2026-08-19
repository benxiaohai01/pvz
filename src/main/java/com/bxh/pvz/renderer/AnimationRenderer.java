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

    /** 绘制阳光：落地后轻微上下浮动，未落地时保持当前下落位置。 */
    public void drawSun(GraphicsContext graphicsContext, Sun sun, double elapsed) {
        double floatingOffset = sun.isGrounded()
                ? Math.sin(elapsed * 2.5 + sun.x() * 0.04) * 2
                : 0;
        double centerX = sun.x();
        double centerY = sun.y() + floatingOffset;
        double sunRadius = GameConfig.SUN_RADIUS;

        graphicsContext.setFill(Color.GOLD);
        graphicsContext.fillOval(
                centerX - sunRadius,
                centerY - sunRadius,
                sunRadius * 2,
                sunRadius * 2);
        graphicsContext.setStroke(Color.ORANGE);
        graphicsContext.setLineWidth(1.5);
        graphicsContext.strokeOval(
                centerX - sunRadius,
                centerY - sunRadius,
                sunRadius * 2,
                sunRadius * 2);

        // 八条光线围绕圆心旋转，让静止在地面上的阳光仍保持视觉活力。
        graphicsContext.setStroke(Color.GOLD);
        for (int rayIndex = 0; rayIndex < 8; rayIndex++) {
            double rayAngle = elapsed * 1.4 + rayIndex * Math.PI / 4;
            double innerX = centerX + Math.cos(rayAngle) * (sunRadius + 1);
            double innerY = centerY + Math.sin(rayAngle) * (sunRadius + 1);
            double outerX = centerX + Math.cos(rayAngle) * (sunRadius + 8);
            double outerY = centerY + Math.sin(rayAngle) * (sunRadius + 8);
            graphicsContext.strokeLine(innerX, innerY, outerX, outerY);
        }
    }

    /** 以豌豆当前位置为圆心绘制子弹。 */
    public void drawPea(GraphicsContext graphicsContext, Projectile projectile) {
        graphicsContext.setFill(Color.web("#7CFC00"));
        graphicsContext.fillOval(
                projectile.x() - GameConfig.PEA_RADIUS,
                projectile.y() - GameConfig.PEA_RADIUS,
                GameConfig.PEA_RADIUS * 2,
                GameConfig.PEA_RADIUS * 2);
    }

    /** 绘制小推车车身与顶部护板。 */
    public void drawLawnCar(GraphicsContext graphicsContext, LawnCar car) {
        double width = 46;
        double height = 26;
        double x = car.x() - width / 2;
        double y = car.y() - height / 2;

        graphicsContext.setFill(Color.web("#1E90FF"));
        graphicsContext.fillRoundRect(x, y, width, height, 6, 6);
        graphicsContext.setFill(Color.web("#0B5BA5"));
        graphicsContext.fillRoundRect(x, y - 7, width, 8, 3, 3);
    }
}
