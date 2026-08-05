package com.pvz.config;

/**
 * 植物攻击行为键，label 为中文名称。
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
