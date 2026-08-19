package com.bxh.pvz.config;

/**
 * 植物攻击行为键，显示名称由枚举的中文标签提供。
 */
public enum AttackBehavior {
    NONE("不攻击"),
    PEA("发射豌豆");

    private final String label;

    AttackBehavior(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
