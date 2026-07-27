package org.bxh.pvz.ecs.system;

import org.bxh.pvz.world.GameWorld;

/**
 * 碰撞系统 —— 空间重叠检测。
 * Phase 2 将实现敌我阵营间的 AABB 碰撞，触发战斗接敌状态。
 */
public final class CollisionSystem implements GameSystem {

    @Override
    public void update(double deltaTime, GameWorld world) {
        // Phase 2 预留：AABB 碰撞检测
    }
}
