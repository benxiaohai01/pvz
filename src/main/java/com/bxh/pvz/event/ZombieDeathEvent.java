package com.bxh.pvz.event;

import com.bxh.pvz.model.entity.zombie.Zombie;

/**
 * 僵尸死亡事件。
 */
public record ZombieDeathEvent(Zombie zombie, DeathCause cause) implements GameEvent {
}
