package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.AttackComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.world.GameWorld;

/**
 * 战斗系统 —— 驱动攻击冷却计时。
 * Phase 1 仅处理冷却递减；实际目标选择与伤害结算将在后续阶段实现。
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
