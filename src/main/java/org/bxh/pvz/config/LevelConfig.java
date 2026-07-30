package org.bxh.pvz.config;

import java.util.List;

public record LevelConfig(int totalWaves, List<WaveData> waves, double initialSun) {
    public static LevelConfig level1() {
        return new LevelConfig(3, List.of(
                new WaveData(3, 12.0, 2.0),
                new WaveData(5, 10.0, 20.0),
                new WaveData(8, 8.0, 20.0)), 150);
    }
}