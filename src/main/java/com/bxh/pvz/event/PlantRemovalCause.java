package com.bxh.pvz.event;

/**
 * 植物移除原因，label 为中文名称。
 */
public enum PlantRemovalCause {
    SHOVEL("铲除"),
    DEATH("死亡");

    private final String label;

    PlantRemovalCause(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
