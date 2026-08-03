package com.pvz.service;

import com.pvz.event.DeathCause;
import com.pvz.event.EventBus;
import com.pvz.event.PlantRemovalCause;
import com.pvz.event.PlantRemovedEvent;
import com.pvz.event.ZombieDeathEvent;
import com.pvz.model.entity.GameObject;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.projectile.Projectile;
import com.pvz.model.entity.zombie.Zombie;

/**
 * 战斗服务：统一处理伤害与死亡，通过事件总线通知外部（分数/UI/音效）。
 */
public final class CombatService {

    private final EventBus eventBus;

    public CombatService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void damage(GameObject target, double amount, GameObject source) {
        switch (target) {
            case Plant plant -> {
                if (plant.isRemoved()) {
                    return;
                }
                plant.takeDamage(amount);
                if (plant.isDead()) {
                    plant.markRemoved();
                    eventBus.publish(new PlantRemovedEvent(plant, PlantRemovalCause.DEATH));
                }
            }
            case Zombie zombie -> {
                if (zombie.isRemoved()) {
                    return;
                }
                zombie.takeDamage(amount);
                if (zombie.isDead()) {
                    zombie.markRemoved();
                    eventBus.publish(new ZombieDeathEvent(zombie, causeOf(source)));
                }
            }
            default -> {
                // 其他对象不受伤害
            }
        }
    }

    public void kill(Zombie zombie, DeathCause cause) {
        if (zombie.isRemoved()) {
            return;
        }
        zombie.markRemoved();
        eventBus.publish(new ZombieDeathEvent(zombie, cause));
    }

    private static DeathCause causeOf(GameObject source) {
        return source instanceof Projectile ? DeathCause.PROJECTILE : DeathCause.LAWN_CAR;
    }
}
