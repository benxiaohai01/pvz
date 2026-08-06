package com.pvz.renderer;

import com.pvz.model.entity.environment.LawnCar;
import com.pvz.model.entity.environment.Sun;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.projectile.Projectile;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.world.GameWorld;
import javafx.scene.canvas.GraphicsContext;

/**
 * 总渲染器：只读世界状态并绘制，绝不修改游戏状态。
 */
public final class GameRenderer {

    private final UIBackgroundRenderer backgroundRenderer = new UIBackgroundRenderer();
    private final PlantRenderer plantRenderer = new PlantRenderer();
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    private final AnimationRenderer animationRenderer = new AnimationRenderer();

    public void draw(GraphicsContext gc, GameWorld world, double elapsed) {
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
