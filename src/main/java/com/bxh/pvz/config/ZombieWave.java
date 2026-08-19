package com.bxh.pvz.config;

import java.util.List;

/**
 * 僵尸波次配置（记录类）：到达时间与有序生成条目列表。
 * 一个波次可混编多种僵尸，例如先 4 只普通僵尸、再 3 只路障僵尸。
 */
public record ZombieWave(
        double startTime,
        List<ZombieSpawn> spawns) {

    public ZombieWave {
        if (startTime < 0) {
            throw new IllegalArgumentException("startTime 不能为负数: " + startTime);
        }
        spawns = List.copyOf(spawns);
        if (spawns.isEmpty()) {
            throw new IllegalArgumentException("波次至少需要一个生成条目");
        }
    }
}
