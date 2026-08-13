package com.bxh.pvz.controller;

import com.bxh.pvz.config.PlantType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantCooldownsTest {

    @Test
    void tracksAndDecrementsCooldowns() {
        PlantCooldowns cooldowns = new PlantCooldowns();

        cooldowns.start(PlantType.SUNFLOWER, 2.5);
        cooldowns.tick(1.0);

        assertEquals(1.5, cooldowns.remaining(PlantType.SUNFLOWER), 0.001);
        cooldowns.tick(2.0);
        assertEquals(0, cooldowns.remaining(PlantType.SUNFLOWER));
    }
}
