package com.bxh.pvz.config;

import java.util.Objects;

/**
 * 波次内的单个生成条目（Record）：某类僵尸生成多少只、间隔多少秒。
 * 一个波次可包含多个条目，从而支持混合僵尸波。
 */
public record ZombieSpawn(ZombieType type, int count, double spawnInterval) {

    public ZombieSpawn {
        Objects.requireNonNull(type, "type");
        if (count <= 0) {
            throw new IllegalArgumentException("count 必须大于 0: " + count);
        }
        if (spawnInterval <= 0) {
            throw new IllegalArgumentException("spawnInterval 必须大于 0: " + spawnInterval);
        }
    }
}
