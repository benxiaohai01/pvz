package com.bxh.pvz.config;

/**
 * 植物产阳光行为键，label 为中文名称。
 */
public enum SunProductionBehavior {
    NONE("不产阳光"),
    PRODUCE_SUN("定时产阳光");

    private final String label;

    SunProductionBehavior(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
