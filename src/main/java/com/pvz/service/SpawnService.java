package com.pvz.service;

import com.pvz.config.GameConfig;
import com.pvz.event.EventBus;
import com.pvz.event.WaveSpawnEvent;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.zombie.Zombie;
import com.pvz.model.level.Level;
import com.pvz.model.level.ZombieWave;
import com.pvz.model.world.GameWorld;

import java.util.Optional;
import java.util.Random;

/**
 * 生成服务：按关卡波次配置定时生成僵尸。
 */
public final class SpawnService {

    private final ZombieFactory zombieFactory;
    private final EventBus eventBus;
    private final Random random = new Random();
    private boolean waveAnnounced;
    private double waveSpawnTimer;
    private int spawnedCount;

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
        Optional<ZombieWave> active = level.activeWave();
        if (active.isEmpty()) {
            return;
        }
        ZombieWave wave = active.get();
        if (!waveAnnounced) {
            waveAnnounced = true;
            waveSpawnTimer = wave.spawnInterval(); // 波次开始时立即生成第一只
            eventBus.publish(new WaveSpawnEvent(level.waveIndex() + 1, level.totalWaves()));
        }
        waveSpawnTimer += delta;
        if (spawnedCount < wave.count() && waveSpawnTimer >= wave.spawnInterval()) {
            waveSpawnTimer = 0;
            spawnZombie(world, wave.zombieType());
            spawnedCount++;
        }
        if (spawnedCount >= wave.count()) {
            level.advanceWave();
            waveAnnounced = false;
            spawnedCount = 0;
            waveSpawnTimer = 0;
        }
    }

    private void spawnZombie(GameWorld world, com.pvz.model.entity.zombie.ZombieType type) {
        int row = random.nextInt(GameConfig.GRID_ROWS);
        Zombie zombie = zombieFactory.create(type, row);
        zombie.placeAtRow(world.lawn().rowCenterY(row));
        world.addZombie(zombie);
    }
}
