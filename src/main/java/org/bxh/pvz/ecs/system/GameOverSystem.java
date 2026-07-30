package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.world.GameWorld;

/**
 * 游戏结束检测系统 —— 僵尸到达房屋线触发失败，所有波次完成+僵尸清空触发胜利。
 */
public final class GameOverSystem implements GameSystem {

    private static final double HOUSE_X = 80.0;
    private final EventBus eventBus;
    private final WaveSystem waveSystem;
    private boolean gameEnded;

    public GameOverSystem(EventBus eventBus, WaveSystem waveSystem) {
        this.eventBus = eventBus;
        this.waveSystem = waveSystem;
    }

    @Override
    public void update(double deltaTime, GameWorld world) {
        if (gameEnded) return;

        // 僵尸到达房屋线 -> 游戏失败
        for (Entity e : world.entities()) {
            if (!(e instanceof ZombieEntity zombie) || !zombie.active()) continue;
            var tf = zombie.getComponent(TransformComponent.class);
            if (tf.isPresent() && tf.get().x() < HOUSE_X) {
                gameEnded = true;
                eventBus.publish(new GameEvent.GameOver(false));
                return;
            }
        }

        // 所有波次完成 + 无僵尸 -> 胜利
        if (waveSystem.allWavesDone() && countActiveZombies(world) == 0) {
            gameEnded = true;
            eventBus.publish(new GameEvent.LevelComplete());
        }
    }

    private long countActiveZombies(GameWorld world) {
        return world.entities().stream()
                .filter(e -> e instanceof ZombieEntity && e.active())
                .count();
    }
}