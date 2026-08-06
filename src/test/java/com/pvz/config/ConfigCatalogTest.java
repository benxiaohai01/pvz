package com.pvz.config;

import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.entity.zombie.ZombieType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigCatalogTest {

    @Test
    void plantDisplayNameComesFromJsonConfig() {
        assertEquals("向日葵", PlantCatalog.of(PlantType.SUNFLOWER).displayName());
        assertEquals("豌豆射手", PlantCatalog.of(PlantType.PEASHOOTER).displayName());
        assertEquals("坚果墙", PlantCatalog.of(PlantType.WALLNUT).displayName());
    }

    @Test
    void zombieDisplayNameComesFromJsonConfig() {
        assertEquals("普通僵尸", ZombieCatalog.of(ZombieType.BASIC).displayName());
        assertEquals("路障僵尸", ZombieCatalog.of(ZombieType.CONEHEAD).displayName());
    }

    @Test
    void plantConfigRejectsBlankDisplayName() {
        assertThrows(IllegalArgumentException.class, () -> new PlantConfig(
                PlantType.SUNFLOWER,
                " ",
                50,
                7.5,
                100,
                0,
                0,
                0,
                0,
                0,
                ColorValue.of("#FFD700"),
                AttackBehavior.NONE,
                SunProductionBehavior.PRODUCE_SUN));
    }
}
