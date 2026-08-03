package com.pvz.event;

import com.pvz.model.entity.zombie.Zombie;

/**
 * 僵尸死亡事件。
 */
public record ZombieDeathEvent(Zombie zombie, DeathCause cause) implements GameEvent {
}
