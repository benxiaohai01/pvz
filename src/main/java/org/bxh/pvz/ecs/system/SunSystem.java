package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.PlantEntity;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.gameplay.SunEntity;
import org.bxh.pvz.world.GameWorld;

/**
 * 阳光系统 —— 管理阳光资源、向日葵生产、天空掉落、点击收集。
 * 阳光数存储在外部状态中（因需跨帧持久化），通过 EventBus 通知 UI 更新。
 */
public final class SunSystem implements GameSystem {

    private final EventBus eventBus;
    private int sunCount;
    private double skyDropTimer;

    public SunSystem(EventBus eventBus, int initialSun) {
        this.eventBus = eventBus;
        this.sunCount = initialSun;
        this.skyDropTimer = 7.0;
    }

    public int sunCount() { return sunCount; }

    /** 消费阳光（种植时扣除），返回是否足够 */
    public boolean spendSun(int amount) {
        if (sunCount >= amount) {
            sunCount -= amount;
            return true;
        }
        return false;
    }

    /** 收集阳光 */
    public void addSun(int amount) {
        sunCount += amount;
        eventBus.publish(new GameEvent.SunCollected(amount));
    }

    @Override
    public void update(double deltaTime, GameWorld world) {
        // 1. 向日葵产阳光
        for (Entity e : world.entities()) {
            if (!(e instanceof PlantEntity plant) || !plant.active()) continue;
            if (plant.plantConfig().sunInterval() <= 0) continue;

            plant.setSunTimer(plant.sunTimer() - deltaTime);
            if (plant.sunTimer() <= 0) {
                var tf = plant.getComponent(TransformComponent.class);
                tf.ifPresent(t -> world.spawnEntity(SunEntity.fromSunflower(t.x(), t.y() - 20)));
                plant.setSunTimer(plant.plantConfig().sunInterval());
            }
        }

        // 2. 天空随机掉落阳光
        skyDropTimer -= deltaTime;
        if (skyDropTimer <= 0) {
            double x = 100 + Math.random() * 700;
            world.spawnEntity(SunEntity.fromSky(x));
            skyDropTimer = 8.0 + Math.random() * 7.0;
        }

        // 3. 阳光到达地面后自动消失
        for (Entity e : world.entities()) {
            if (!(e instanceof SunEntity sun) || !sun.active()) continue;
            var tf = sun.getComponent(TransformComponent.class);
            if (tf.isPresent() && tf.get().y() > 650) {
                world.destroyEntity(sun);
            }
        }
    }
}