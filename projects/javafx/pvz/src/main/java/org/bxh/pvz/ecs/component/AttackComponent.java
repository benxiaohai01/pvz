package org.bxh.pvz.ecs.component;

/**
 * 攻击组件 —— 攻击属性与冷却计时。
 * 由 CombatSystem 消费。
 */
public final class AttackComponent implements Component {

    private double damage;
    private double attackRange;
    private double attackCooldown;
    private double cooldownTimer;

    public AttackComponent(double damage, double attackRange, double attackCooldown) {
        this.damage = damage;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.cooldownTimer = 0;
    }

    public double damage() { return damage; }
    public double attackRange() { return attackRange; }
    public double attackCooldown() { return attackCooldown; }
    public double cooldownTimer() { return cooldownTimer; }

    /** 每帧减少冷却时间 */
    public void tickCooldown(double deltaTime) {
        if (cooldownTimer > 0) {
            cooldownTimer = Math.max(0, cooldownTimer - deltaTime);
        }
    }

    /** 是否可以攻击（冷却完毕） */
    public boolean canAttack() {
        return cooldownTimer <= 0;
    }

    /** 攻击后重置冷却 */
    public void resetCooldown() {
        cooldownTimer = attackCooldown;
    }
}
