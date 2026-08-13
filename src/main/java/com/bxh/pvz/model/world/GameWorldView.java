package com.bxh.pvz.model.world;

import com.bxh.pvz.model.entity.environment.LawnCar;
import com.bxh.pvz.model.entity.environment.Sun;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.model.entity.projectile.Projectile;
import com.bxh.pvz.model.entity.zombie.Zombie;

import java.util.List;

/**
 * Read-only projection of a running game world for rendering and presentation.
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
