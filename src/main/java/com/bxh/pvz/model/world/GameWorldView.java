package com.bxh.pvz.model.world;

import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;

import java.util.List;

/**
 * 运行中游戏世界的只读投影，供渲染与展示使用。
 */
public interface GameWorldView {

    List<Zombie> zombies();

    List<Projectile> projectiles();

    List<Sun> suns();

    List<LawnCar> cars();

    List<Plant> plants();

    int sun();

    boolean isOver();
}
