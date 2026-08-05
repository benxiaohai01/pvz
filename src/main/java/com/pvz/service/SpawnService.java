package com.pvz.service;

import com.pvz.config.GameConfig;
import com.pvz.event.EventBus;
import com.pvz.event.WaveSpawnEvent;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.level.Level;
import com.pvz.model.level.ZombieSpawn;
import com.pvz.model.world.GameWorld;

import java.util.Optional;
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
        level.advance(delta);
        if (level.allWavesSpawned()) {
            return;
        }
        if (!level.isWaveActive()) {
            return;
        }
        if (!level.isWaveAnnounced()) {
            level.announceWave();
            eventBus.publish(new WaveSpawnEvent(level.waveIndex() + 1, level.totalWaves()));
        }
        Optional<ZombieSpawn> spawn = level.currentSpawn();
        if (spawn.isEmpty()) {
            level.completeWave();
            return;
        }
        level.advanceSpawnTimer(delta);
        ZombieSpawn current = spawn.get();
        if (level.spawnedInEntry() == 0 || level.spawnTimer() >= current.spawnInterval()) {
            level.resetSpawnTimer();
            spawnZombie(world, current.type());
            level.consumeSpawn();
        }
    }

    private void spawnZombie(GameWorld world, com.pvz.model.entity.zombie.ZombieType type) {
        int row = random.nextInt(GameConfig.GRID_ROWS);
        Zombie zombie = zombieFactory.create(type, row);
        zombie.placeAtRow(world.lawn().rowCenterY(row));
        world.addZombie(zombie);
    }
}
