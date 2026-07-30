package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.gameplay.LawnMowerEntity;
import org.bxh.pvz.world.GameWorld;

/**
 * 【设计模式：状态模式（State）—— 小推车 READY/RUNNING/USED】
 * 小推车系统 —— 僵尸进入左侧触发区域时启动，高速扫除该行所有僵尸。
 */
public final class LawnMowerSystem implements GameSystem {

    private static final double TRIGGER_X = 150.0; // 触发线

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity e : world.entities()) {
            if (!(e instanceof LawnMowerEntity mower) || !mower.active()) continue;

            switch (mower.state()) {
                case READY -> checkTrigger(mower, world);
                case RUNNING -> runMower(mower, world);
                case USED -> {} // 已使用，什么都不做
            }
        }
    }

    /** 检测是否有僵尸进入触发区域 */
    private void checkTrigger(LawnMowerEntity mower, GameWorld world) {
        for (Entity e : world.entities()) {
            if (!(e instanceof ZombieEntity zombie) || !zombie.active()) continue;
            var ztf = zombie.getComponent(TransformComponent.class);
            if (ztf.isEmpty()) continue;

            int zRow = (int) ((ztf.get().y() - world.gridMap().offsetY()) / world.gridMap().cellSize());
            if (zRow == mower.row() && ztf.get().x() < TRIGGER_X) {
                mower.setState(LawnMowerEntity.State.RUNNING);
                var mv = mower.getComponent(MovementComponent.class);
                mv.ifPresent(m -> m.setVelocity(400, 0));
                return;
            }
        }
    }

    /** 小推车运行中：横扫该行僵尸 */
    private void runMower(LawnMowerEntity mower, GameWorld world) {
        var tf = mower.getComponent(TransformComponent.class);
        if (tf.isEmpty()) return;

        // 超出右边界 -> 标记为已使用
        if (tf.get().x() > 1100) {
            mower.setState(LawnMowerEntity.State.USED);
            mower.setActive(false);
            return;
        }

        // 碾压该行所有僵尸
        for (Entity e : world.entities()) {
            if (!(e instanceof ZombieEntity zombie) || !zombie.active()) continue;
            var ztf = zombie.getComponent(TransformComponent.class);
            if (ztf.isEmpty()) continue;

            int zRow = (int) ((ztf.get().y() - world.gridMap().offsetY()) / world.gridMap().cellSize());
            if (zRow == mower.row() && Math.abs(ztf.get().x() - tf.get().x()) < 80) {
                world.destroyEntity(zombie);
            }
        }
    }
}