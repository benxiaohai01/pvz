package com.pvz.model.level;

import com.pvz.model.entity.zombie.ZombieType;

/**
 * 僵尸波次配置（Record）：到达时间、僵尸类型、数量、生成间隔。
 */
public record ZombieWave(
        double startTime,
        ZombieType zombieType,
        int count,
        double spawnInterval) {
}
