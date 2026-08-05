package com.pvz.factory;

import com.pvz.config.AttackBehavior;
import com.pvz.config.MoveBehavior;
import com.pvz.config.PlantCatalog;
import com.pvz.config.SunProductionBehavior;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.strategy.MoveLeftStrategy;
import com.pvz.strategy.NoAttackStrategy;
import com.pvz.strategy.NoSunProductionStrategy;
import com.pvz.strategy.PeaAttackStrategy;
import com.pvz.strategy.ProduceSunStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BehaviorCatalogTest {

    @Test
    void attackBehaviorMapsToStrategy() {
        var peashooter = PlantCatalog.of(PlantType.PEASHOOTER);
        var sunflower = PlantCatalog.of(PlantType.SUNFLOWER);

        assertInstanceOf(PeaAttackStrategy.class,
                BehaviorCatalog.attackFor(AttackBehavior.PEA, peashooter));
        assertInstanceOf(NoAttackStrategy.class,
                BehaviorCatalog.attackFor(AttackBehavior.NONE, sunflower));
    }

    @Test
    void sunBehaviorMapsToStrategy() {
        var sunflower = PlantCatalog.of(PlantType.SUNFLOWER);
        var peashooter = PlantCatalog.of(PlantType.PEASHOOTER);

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
