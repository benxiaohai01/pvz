package org.bxh.pvz.ecs.system;

import org.bxh.pvz.config.LevelConfig;
import org.bxh.pvz.config.WaveData;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.world.GameWorld;

/**
 * 【设计模式：状态模式（State）—— 波次状态管理】
 * 波次系统 —— 管理僵尸波次生成。按 LevelConfig 定义的波次顺序生成僵尸。
 */
public final class WaveSystem implements GameSystem {

    private final LevelConfig levelConfig;
    private int currentWave;
    private int zombiesSpawnedInWave;
    private double waveDelayTimer;
    private double spawnTimer;
    private boolean allWavesDone;

    public WaveSystem(LevelConfig levelConfig) {
        this.levelConfig = levelConfig;
        this.currentWave = 0;
        this.waveDelayTimer = levelConfig.waves().get(0).delayBeforeWave();
        this.allWavesDone = false;
    }

    public boolean allWavesDone() { return allWavesDone; }
    public int currentWave() { return currentWave; }

    @Override
    public void update(double deltaTime, GameWorld world) {
        if (allWavesDone) return;

        if (currentWave >= levelConfig.waves().size()) {
            allWavesDone = true;
            return;
        }

        WaveData wave = levelConfig.waves().get(currentWave);

        // 波次间延迟
        if (waveDelayTimer > 0) {
            waveDelayTimer -= deltaTime;
            return;
        }

        // 生成僵尸
        spawnTimer -= deltaTime;
        if (spawnTimer <= 0 && zombiesSpawnedInWave < wave.zombieCount()) {
            spawnZombie(world);
            zombiesSpawnedInWave++;
            spawnTimer = wave.spawnInterval();
        }

        // 本波次生成完毕 -> 进入下一波
        if (zombiesSpawnedInWave >= wave.zombieCount()) {
            currentWave++;
            zombiesSpawnedInWave = 0;
            spawnTimer = 0;
            if (currentWave < levelConfig.waves().size()) {
                waveDelayTimer = levelConfig.waves().get(currentWave).delayBeforeWave();
            } else {
                allWavesDone = true;
            }
        }
    }

    private void spawnZombie(GameWorld world) {
        int row = (int) (Math.random() * world.gridMap().rows());
        double x = world.gridMap().cellToScreenX(world.gridMap().cols() - 1);
        double y = world.gridMap().cellToScreenY(row);
        world.spawnEntity(ZombieEntity.createBasicZombie(x, y));
    }
}