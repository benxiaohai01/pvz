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

    public void draw(GraphicsContext gc, GameWorldView world, double elapsed) {
        backgroundRenderer.draw(gc);

        for (Plant plant : world.plants()) {
            if (!plant.isRemoved()) {
                plantRenderer.draw(gc, plant, elapsed);
            }
        }
        for (Zombie zombie : world.zombies()) {
            if (!zombie.isRemoved()) {
                zombieRenderer.draw(gc, zombie);
            }
        }
        for (Projectile projectile : world.projectiles()) {
            if (!projectile.isRemoved()) {
                animationRenderer.drawPea(gc, projectile);
            }
        }
        for (LawnCar car : world.cars()) {
            if (!car.isRemoved()) {
                animationRenderer.drawLawnCar(gc, car);
            }
        }
        for (Sun sun : world.suns()) {
            if (!sun.isRemoved()) {
                animationRenderer.drawSun(gc, sun, elapsed);
            }
        }
    }
}
