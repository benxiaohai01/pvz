package com.bxh.pvz.event;

/**
 * 植物移除原因，显示名称由枚举的中文标签提供。
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
