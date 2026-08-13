package com.bxh.pvz.strategy;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.model.entity.plant.Plant;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantStrategyTest {

    @Test
    void sunflowerProducesSunThroughStrategy() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Plant sunflower = TestFixture.plantFactory().create(PlantType.SUNFLOWER, 0, 0);
        world.placePlant(sunflower);

        world.update(5.0);

        assertEquals(1, world.suns().size());
    }

    @Test
    void peashooterFiresAtZombieInSameRow() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Plant peashooter = TestFixture.plantFactory().create(PlantType.PEASHOOTER, 1, 1);
        world.placePlant(peashooter);

        var zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 1);
        zombie.placeAtRow(world.lawn().rowCenterY(1));
        zombie.setPosition(peashooter.x() + 200, peashooter.y());
        world.addZombie(zombie);

        world.update(2.0);

        assertEquals(1, world.projectiles().size());
    }
}
