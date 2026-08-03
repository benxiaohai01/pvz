package com.pvz.strategy;

import com.pvz.model.entity.GameObject;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.world.GameWorld;

import java.util.Optional;

/**
 * 索敌策略（Strategy Pattern）：植物如何选取目标。
 */
public interface TargetStrategy<T extends GameObject> {

    Optional<T> findTarget(Plant source, GameWorld world);
}
