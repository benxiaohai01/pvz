package com.bxh.pvz.event;

import com.bxh.pvz.model.entity.plant.Plant;

/**
 * 植物被移除事件（铲除或死亡）。
 */
public record PlantRemovedEvent(Plant plant, PlantRemovalCause cause) implements GameEvent {
}
