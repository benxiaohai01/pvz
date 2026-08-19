package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.GameObject;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.world.GameWorld;

import java.util.Optional;

/**
 * 索敌策略（策略模式）：植物如何选取目标。
 */
public interface TargetStrategy<T extends GameObject> {

    Optional<T> findTarget(Plant source, GameWorld world);
}
