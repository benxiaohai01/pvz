package com.pvz.event;

/**
 * 僵尸死亡原因，label 为中文名称。
 */
public enum DeathCause {
    PROJECTILE("子弹击杀"),
    LAWN_CAR("小推车击杀");

    private final String label;

    DeathCause(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
