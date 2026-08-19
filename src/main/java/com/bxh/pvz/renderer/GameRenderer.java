package com.bxh.pvz.renderer;

import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorldView;
import javafx.scene.canvas.GraphicsContext;

/**
 * 总渲染器：只读世界状态并绘制，绝不修改游戏状态。
 */
public final class GameRenderer {

    private final UIBackgroundRenderer backgroundRenderer;
    private final PlantRenderer plantRenderer;
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    private final AnimationRenderer animationRenderer = new AnimationRenderer();

    public GameRenderer(SpriteCatalog sprites) {
        this.backgroundRenderer = new UIBackgroundRenderer(sprites);
        this.plantRenderer = new PlantRenderer(sprites);
    }

    /** 只绘制整张背景图片或图片缺失时的棋盘占位背景。 */
    public void drawBackground(GraphicsContext graphicsContext) {
        backgroundRenderer.draw(graphicsContext);
    }

    /**
     * 按植物、僵尸、子弹、小推车、阳光的顺序绘制当前世界。
     * 每帧先清空对象画布，再绘制最新状态，避免旧帧残影。
     */
    public void drawWorld(GraphicsContext graphicsContext, GameWorldView world, double elapsed) {
        graphicsContext.clearRect(
                0,
                0,
                graphicsContext.getCanvas().getWidth(),
                graphicsContext.getCanvas().getHeight());
        for (Plant plant : world.plants()) {
            if (!plant.isRemoved()) {
                plantRenderer.draw(graphicsContext, plant, elapsed);
            }
        }
        for (Zombie zombie : world.zombies()) {
            if (!zombie.isRemoved()) {
                zombieRenderer.draw(graphicsContext, zombie);
            }
        }
        for (Projectile projectile : world.projectiles()) {
            if (!projectile.isRemoved()) {
                animationRenderer.drawPea(graphicsContext, projectile);
            }
        }
        for (LawnCar car : world.cars()) {
            if (!car.isRemoved()) {
                animationRenderer.drawLawnCar(graphicsContext, car);
            }
        }
        for (Sun sun : world.suns()) {
            if (!sun.isRemoved()) {
                animationRenderer.drawSun(graphicsContext, sun, elapsed);
            }
        }
    }
}
