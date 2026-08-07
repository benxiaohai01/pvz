package com.pvz.strategy;

import com.pvz.config.LevelCatalog;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.plant.Plant;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.entity.zombie.ZombieType;
import com.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantStrategyTest {

    @Test
    void sunflowerProducesSunThroughStrategy() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        Plant sunflower = new PlantFactory().create(PlantType.SUNFLOWER, 0, 0);
        world.placePlant(sunflower);

        world.update(5.0);

        assertEquals(1, world.suns().size());
    }

    @Test
    void peashooterFiresAtZombieInSameRow() {
        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        Plant peashooter = new PlantFactory().create(PlantType.PEASHOOTER, 1, 1);
        world.placePlant(peashooter);

        var zombie = new ZombieFactory().create(ZombieType.BASIC, 1);
        zombie.placeAtRow(world.lawn().rowCenterY(1));
        zombie.setPosition(peashooter.x() + 200, peashooter.y());
        world.addZombie(zombie);

        world.update(2.0);

        assertEquals(1, world.projectiles().size());
    }
}
