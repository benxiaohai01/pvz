package org.bxh.pvz.ecs.system;

import org.bxh.pvz.world.GameWorld;

/**
 * 【设计模式：系统模式（System Pattern）—— ECS 架构中的 "S"】
 * 游戏系统契约接口。
 */
public sealed interface GameSystem
        permits MovementSystem,
                CombatSystem,
                CollisionSystem,
                RenderSystem,
                SunSystem,
                LawnMowerSystem,
                WaveSystem,
                GameOverSystem {

    void update(double deltaTime, GameWorld world);
}