package com.pvz.event;

/**
 * 游戏事件（Sealed Interface）：观察者模式的事件载体。
 */
public sealed interface GameEvent permits GameOverEvent, PlantRemovedEvent, SunCollectedEvent, WaveSpawnEvent, ZombieDeathEvent {
}
