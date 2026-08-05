package com.pvz.config;

/**
 * 僵尸移动行为键，label 为中文名称。
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
