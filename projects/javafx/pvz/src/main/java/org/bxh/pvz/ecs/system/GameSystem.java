package org.bxh.pvz.ecs.system;

import org.bxh.pvz.world.GameWorld;

/**
 * 【设计模式：系统模式（System Pattern）—— ECS 架构中的 "S"】
 * 游戏系统契约接口。系统包含所有运行时逻辑，组件仅持有数据。
 * 每个系统每帧处理匹配其 Archetype 的实体。
 */
public sealed interface GameSystem
        permits MovementSystem,
                CombatSystem,
                CollisionSystem,
                RenderSystem {

    /** 每帧调用，deltaTime 单位为秒 */
    void update(double deltaTime, GameWorld world);
}
