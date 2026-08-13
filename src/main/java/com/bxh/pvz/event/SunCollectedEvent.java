package com.bxh.pvz.event;

import com.bxh.pvz.model.entity.environment.Sun;

/**
 * 阳光收集事件。
 */
public record SunCollectedEvent(Sun sun, int amount) implements GameEvent {
}
