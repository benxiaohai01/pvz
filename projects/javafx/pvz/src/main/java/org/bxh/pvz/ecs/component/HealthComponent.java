package org.bxh.pvz.ecs.component;

/**
 * 生命值组件 —— 持有可变的生命状态。
 * 由 CombatSystem 修改，其他系统只读查询。
 */
public final class HealthComponent implements Component {

    private double currentHealth;
    private final double maxHealth;

    public HealthComponent(double maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public double currentHealth() { return currentHealth; }
    public double maxHealth() { return maxHealth; }
    public boolean isAlive() { return currentHealth > 0; }

    /** 造成伤害，返回是否死亡 */
    public boolean takeDamage(double amount) {
        currentHealth = Math.max(0, currentHealth - amount);
        return !isAlive();
    }

    /** 恢复生命值 */
    public void heal(double amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }
}
