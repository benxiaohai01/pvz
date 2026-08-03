package com.pvz.model.level;

import java.util.Optional;

/**
 * 关卡运行时状态：推进时间、当前波次、剩余生成数量。
 */
public final class Level {

    private final LevelConfig config;
    private int waveIndex;
    private double elapsed;

    public Level(LevelConfig config) {
        this.config = config;
    }

    public LevelConfig config() {
        return config;
    }

    public double elapsed() {
        return elapsed;
    }

    public void advance(double delta) {
        elapsed += delta;
    }

    /** 当前应激活的波次（顺序波次：上一波生成完才轮到下一波）。 */
    public Optional<ZombieWave> activeWave() {
        if (waveIndex >= config.waves().size()) {
            return Optional.empty();
        }
        ZombieWave wave = config.waves().get(waveIndex);
        return elapsed >= wave.startTime() ? Optional.of(wave) : Optional.empty();
    }

    public void advanceWave() {
        waveIndex++;
    }

    public boolean allWavesSpawned() {
        return waveIndex >= config.waves().size();
    }

    public int waveIndex() {
        return waveIndex;
    }

    public int totalWaves() {
        return config.waves().size();
    }
}
