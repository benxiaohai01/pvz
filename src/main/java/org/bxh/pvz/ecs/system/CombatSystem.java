package org.bxh.pvz.ecs.system;

import org.bxh.pvz.ecs.component.AttackComponent;
import org.bxh.pvz.ecs.component.HealthComponent;
import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.PlantEntity;
import org.bxh.pvz.ecs.entity.ZombieEntity;
import org.bxh.pvz.gameplay.BulletEntity;
import org.bxh.pvz.world.GameWorld;

/**
 * 战斗系统 —— 冷却递减 + 植物射击 + 僵尸近战（含停止/恢复移动）。
 * 僵尸遇植物时停止前进，植物死亡后恢复移动。
 */
public final class CombatSystem implements GameSystem {

    private static final double ROW_TOLERANCE = 30.0;
    private static final double ZOMBIE_MELEE_RANGE = 50.0;

    @Override
    public void update(double deltaTime, GameWorld world) {
        for (Entity entity : world.entities()) {
            if (!entity.active()) continue;

            // 冷却递减
            entity.getComponent(AttackComponent.class)
                  .ifPresent(a -> a.tickCooldown(deltaTime));

            // 按实体类型执行攻击行为
            switch (entity) {
                case PlantEntity plant -> plantShoot(plant, world);
                case ZombieEntity zombie -> zombieEngage(zombie, world);
                default -> {}
            }
        }
    }

    /** 植物射击：同行最近僵尸进入射程则发射子弹 */
    private void plantShoot(PlantEntity plant, GameWorld world) {
        var atk = plant.getComponent(AttackComponent.class);
        if (atk.isEmpty() || !atk.get().canAttack()) return;
        var tf = plant.getComponent(TransformComponent.class);
        if (tf.isEmpty()) return;

        Entity target = findNearestZombieInRow(plant, world);
        if (target == null) return;

        var a = atk.get();
        var t = tf.get();
        world.spawnEntity(BulletEntity.create(t.x() + 16, t.y(), a.damage(), 300));
        a.resetCooldown();
    }

    /**
     * 僵尸接敌逻辑：
     *   1. 检测前方最近植物是否进入近战范围
     *   2. 在范围内 → 停止移动，冷却就绪则攻击
     *   3. 不在范围内 → 恢复移动（如果之前被停止）
     *   4. 植物死亡 → 恢复移动
     */
    private void zombieEngage(ZombieEntity zombie, GameWorld world) {
        var atk = zombie.getComponent(AttackComponent.class);
        var ztf = zombie.getComponent(TransformComponent.class);
        var zmv = zombie.getComponent(MovementComponent.class);
        if (ztf.isEmpty()) return;

        PlantEntity target = findPlantInMeleeRange(zombie, ztf.get(), world);

        if (target != null) {
            // 在近战范围内：停止移动
            zmv.ifPresent(m -> m.setVelocity(0, 0));

            // 冷却就绪则攻击
            if (atk.isPresent() && atk.get().canAttack()) {
                target.getComponent(HealthComponent.class).ifPresent(hp -> {
                    boolean dead = hp.takeDamage(atk.get().damage());
                    atk.get().resetCooldown();
                    if (dead) {
                        world.destroyEntity(target);
                        // 植物死亡：恢复移动
                        zmv.ifPresent(m -> m.setVelocity(-m.speed(), 0));
                    }
                });
            }
        } else {
            // 没有植物在范围内：如果之前被停止了就恢复移动
            zmv.ifPresent(m -> {
                if (m.velocityX() == 0) {
                    m.setVelocity(-m.speed(), 0);
                }
            });
        }
    }

    /** 寻找近战范围内最近的植物（僵尸前方） */
    private PlantEntity findPlantInMeleeRange(ZombieEntity zombie, TransformComponent ztf, GameWorld world) {
        PlantEntity best = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : world.entities()) {
            if (!(e instanceof PlantEntity plant) || !plant.active()) continue;
            var ptf = plant.getComponent(TransformComponent.class);
            if (ptf.isEmpty()) continue;

            double dx = ztf.x() - ptf.get().x(); // 僵尸在植物右侧时 dx > 0
            double dy = Math.abs(ztf.y() - ptf.get().y());

            // 僵尸必须在植物右侧（正在接近），且在近战范围内
            if (dx > 0 && dx < ZOMBIE_MELEE_RANGE && dy < ROW_TOLERANCE && dx < bestDist) {
                bestDist = dx;
                best = plant;
            }
        }
        return best;
    }

    /** 寻找同行中最近且在射程内的僵尸 */
    private Entity findNearestZombieInRow(PlantEntity plant, GameWorld world) {
        var tf = plant.getComponent(TransformComponent.class);
        var atk = plant.getComponent(AttackComponent.class);
        if (tf.isEmpty() || atk.isEmpty()) return null;

        double px = tf.get().x(), py = tf.get().y();
        double range = atk.get().attackRange();

        Entity best = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : world.entities()) {
            if (!(e instanceof ZombieEntity zombie) || !zombie.active()) continue;
            var ztf = zombie.getComponent(TransformComponent.class);
            if (ztf.isEmpty()) continue;

            double dx = ztf.get().x() - px;
            double dy = Math.abs(ztf.get().y() - py);

            if (dx > 0 && dx <= range && dy < ROW_TOLERANCE && dx < bestDist) {
                bestDist = dx;
                best = zombie;
            }
        }
        return best;
    }
}