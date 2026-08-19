package com.bxh.pvz.controller;

import com.bxh.pvz.config.PlantType;

import java.util.EnumMap;
import java.util.Map;

/**
 * 记录每种植物卡片的冷却剩余时间。
 */
public final class PlantCooldowns {

    /** 每种植物卡片剩余的冷却秒数，不存在的类型按零处理。 */
    private final Map<PlantType, Double> remainingSecondsByType = new EnumMap<>(PlantType.class);

    /** 查询指定植物当前剩余的冷却秒数。 */
    public double remaining(PlantType type) {
        return remainingSecondsByType.getOrDefault(type, 0.0);
    }

    /** 种植成功后把指定植物的冷却时间重置为完整冷却秒数。 */
    public void start(PlantType type, double cooldown) {
        remainingSecondsByType.put(type, cooldown);
    }

    /** 每帧扣减所有冷却时间，最小值固定为 0。 */
    public void tick(double delta) {
        remainingSecondsByType.replaceAll(
                (plantType, remainingSeconds) -> Math.max(0, remainingSeconds - delta));
    }
}
