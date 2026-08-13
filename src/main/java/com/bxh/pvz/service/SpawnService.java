package com.bxh.pvz.service;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.WaveSpawnEvent;
import com.bxh.pvz.factory.ZombieFactory;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.level.Level;
import com.bxh.pvz.model.world.GameWorld;

import java.util.Random;

/**
 * 生成服务：驱动关卡的波次状态，按配置定时生成僵尸。
 */
public final class SpawnService {

    private final ZombieFactory zombieFactory;
    private final EventBus eventBus;
    private final Random random = new Random();

    public SpawnService(ZombieFactory zombieFactory, EventBus eventBus) {
        this.zombieFactory = zombieFactory;
        this.eventBus = eventBus;
    }

    public void update(GameWorld world, double delta) {
        Level level = world.level();
        Level.SpawnTick tick = level.tickSpawn(delta);
        if (tick.isQuiet()) {
            return;
        }
        if (tick.waveAnnounced()) {
            eventBus.publish(new WaveSpawnEvent(level.waveIndex() + 1, level.totalWaves()));
        }
        if (tick.spawnedType() == null) {
            return;
        }
        spawnZombie(world, tick.spawnedType());
    }

    private void spawnZombie(GameWorld world, ZombieType type) {
        int row = random.nextInt(GameConfig.GRID_ROWS);
        Zombie zombie = zombieFactory.create(type, row);
        zombie.placeAtRow(world.lawn().rowCenterY(row));
        world.addZombie(zombie);
    }
}
