package com.bxh.pvz.renderer;

import com.bxh.pvz.model.entity.zombie.Zombie;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 僵尸渲染：灰色方块 + 头部。
 */
public final class ZombieRenderer {

    /** 以僵尸当前位置为中心绘制身体、头部、眼睛和攻击中的嘴。 */
    public void draw(GraphicsContext graphicsContext, Zombie zombie) {
        double width = 34;
        double height = 58;
        double x = zombie.x() - width / 2;
        double y = zombie.y() - height / 2;

        graphicsContext.setFill(RendererColors.of(zombie.config().color()));
        graphicsContext.fillRoundRect(x, y, width, height, 8, 8);

        graphicsContext.setFill(Color.web("#5A5A5A"));
        graphicsContext.fillOval(x + width * 0.22, y - 10, width * 0.56, height * 0.30);

        graphicsContext.setFill(Color.web("#B71C1C"));
        graphicsContext.fillOval(x + width * 0.34, y - 6, 5, 5);
        graphicsContext.fillOval(x + width * 0.58, y - 6, 5, 5);

        if (zombie.isAttacking()) {
            graphicsContext.setFill(Color.web("#3E2723"));
            graphicsContext.fillOval(x + width * 0.42, y + height * 0.55, width * 0.16, height * 0.22);
        }
    }
}
