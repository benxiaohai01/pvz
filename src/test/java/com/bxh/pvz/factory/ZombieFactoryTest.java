package com.bxh.pvz.factory;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.MoveBehavior;
import com.bxh.pvz.model.entity.zombie.Zombie;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZombieFactoryTest {

    @Test
    void createsConfiguredZombieTypes() {
        ZombieFactory factory = TestFixture.zombieFactory();

        Zombie basic = factory.create(ZombieType.BASIC, 2);
        Zombie conehead = factory.create(ZombieType.CONEHEAD, 3);

        assertEquals(ZombieType.BASIC, basic.config().type());
        assertEquals(ZombieType.CONEHEAD, conehead.config().type());
        assertEquals(100, basic.config().maxHp());
        assertEquals(200, conehead.config().maxHp());
        assertEquals(2, basic.row());
        assertEquals(3, conehead.row());
        assertEquals(MoveBehavior.MOVE_LEFT, basic.config().moveBehavior());
        assertEquals(MoveBehavior.MOVE_LEFT, conehead.config().moveBehavior());
    }

    @Test
    void zombieMovesLeftFromConfiguredBehavior() {
        GameWorld world = new GameWorld(TestFixture.level("1-1"));
        Zombie zombie = TestFixture.zombieFactory().create(ZombieType.BASIC, 0);
        zombie.placeAtRow(world.lawn().rowCenterY(0));
        world.addZombie(zombie);

        double before = zombie.x();
        zombie.update(world, 1.0);
        assertEquals(before - zombie.speed() * 1.0, zombie.x(), 0.001);
    }
}
