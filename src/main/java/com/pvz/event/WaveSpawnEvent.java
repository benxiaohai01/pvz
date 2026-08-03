package com.pvz.event;

/**
 * 波次开始事件。
 */
public record WaveSpawnEvent(int waveIndex, int totalWaves) implements GameEvent {
}
