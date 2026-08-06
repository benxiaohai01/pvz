package com.pvz.model.entity.plant;

/**
 * 植物类型（数据驱动配置的稳定键）。
 * 中文名称属于显示数据，配置在 config/plants.json 的 displayName 字段。
 */
public enum PlantType {
    /** 向日葵 */
    SUNFLOWER,
    /** 豌豆射手 */
    PEASHOOTER,
    /** 坚果墙 */
    WALLNUT;
}
