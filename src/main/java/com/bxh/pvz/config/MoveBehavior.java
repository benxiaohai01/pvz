package com.bxh.pvz.config;

/**
 * 僵尸移动行为键，显示名称由枚举的中文标签提供。
 */
public enum MoveBehavior {
    MOVE_LEFT("向左移动");

    private final String label;

    MoveBehavior(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
