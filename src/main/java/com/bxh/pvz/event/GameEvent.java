package com.bxh.pvz.event;

/**
 * 游戏事件（密封接口）：观察者模式的事件载体。
 */
public sealed interface GameEvent permits GameOverEvent, PlantRemovedEvent, SunCollectedEvent, WaveSpawnEvent, ZombieDeathEvent {
}
