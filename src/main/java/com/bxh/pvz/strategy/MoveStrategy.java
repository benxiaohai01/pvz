package com.bxh.pvz.strategy;

import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 移动策略（Strategy Pattern）：决定僵尸何时能移动。
 */
public interface MoveStrategy {

    boolean canMove(Zombie zombie, GameWorld world);
}
