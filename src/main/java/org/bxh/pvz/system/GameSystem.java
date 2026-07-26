package org.bxh.pvz.system;

import org.bxh.pvz.world.GameWorld;

/**
 * Game system contract -- the "S" in ECS.
 * Systems contain all runtime logic; components hold only data.
 * Each system processes entities that match its archetype each frame.
 */
public sealed interface GameSystem
        permits MovementSystem,
                CombatSystem,
                CollisionSystem,
                RenderSystem {

    void update(double deltaTime, GameWorld world);
}
