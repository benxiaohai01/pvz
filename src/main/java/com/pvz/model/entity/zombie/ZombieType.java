package com.pvz.model.entity.zombie;

/**
 * 僵尸类型（数据驱动配置的键），label 为中文名称。
 */
public enum ZombieType {
    BASIC("普通僵尸"),
    CONEHEAD("路障僵尸");

    private final String label;

    ZombieType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
