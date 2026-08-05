package com.pvz.model.entity.plant;

/**
 * 植物类型（数据驱动配置的键），label 为中文名称。
 */
public enum PlantType {
    SUNFLOWER("向日葵"),
    PEASHOOTER("豌豆射手"),
    WALLNUT("坚果墙");

    private final String label;

    PlantType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
