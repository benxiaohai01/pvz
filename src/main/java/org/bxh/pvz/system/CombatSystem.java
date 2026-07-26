package org.bxh.pvz.system;

import org.bxh.pvz.component.AttackComponent;
import org.bxh.pvz.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * Drives attack cooldowns. Phase 1 stub -- cooldown ticking works;
 * actual targeting deferred to a future gameplay-layer system.
 */
public final class CombatSystem implements GameSystem {

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;

            var attack = entity.getComponent(AttackComponent.class);
            if (attack.isPresent()) {
                attack.get().tickCooldown(deltaTime);
            }
        }
    }
}
