package com.bxh.pvz.factory;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.AttackBehavior;
import com.bxh.pvz.config.MoveBehavior;
import com.bxh.pvz.config.SunProductionBehavior;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.strategy.MoveLeftStrategy;
import com.bxh.pvz.strategy.NoAttackStrategy;
import com.bxh.pvz.strategy.NoSunProductionStrategy;
import com.bxh.pvz.strategy.PeaAttackStrategy;
import com.bxh.pvz.strategy.ProduceSunStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BehaviorCatalogTest {

    @Test
    void attackBehaviorMapsToStrategy() {
        var peashooter = TestFixture.plants().of(PlantType.PEASHOOTER);
        var sunflower = TestFixture.plants().of(PlantType.SUNFLOWER);

        assertInstanceOf(PeaAttackStrategy.class,
                BehaviorCatalog.attackFor(AttackBehavior.PEA, peashooter));
        assertInstanceOf(NoAttackStrategy.class,
                BehaviorCatalog.attackFor(AttackBehavior.NONE, sunflower));
    }

    @Test
    void sunBehaviorMapsToStrategy() {
        var sunflower = TestFixture.plants().of(PlantType.SUNFLOWER);
        var peashooter = TestFixture.plants().of(PlantType.PEASHOOTER);

        assertInstanceOf(ProduceSunStrategy.class,
                BehaviorCatalog.sunProductionFor(SunProductionBehavior.PRODUCE_SUN, sunflower));
        assertInstanceOf(NoSunProductionStrategy.class,
                BehaviorCatalog.sunProductionFor(SunProductionBehavior.NONE, peashooter));
    }

    @Test
    void moveBehaviorMapsToStrategy() {
        assertInstanceOf(MoveLeftStrategy.class, BehaviorCatalog.moveFor(MoveBehavior.MOVE_LEFT));
    }
}
