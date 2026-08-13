package com.bxh.pvz.config;

import com.bxh.pvz.TestFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigCatalogTest {

    @Test
    void plantDisplayNameComesFromJsonConfig() {
        assertEquals("向日葵", TestFixture.plants().of(PlantType.SUNFLOWER).displayName());
        assertEquals("豌豆射手", TestFixture.plants().of(PlantType.PEASHOOTER).displayName());
        assertEquals("坚果墙", TestFixture.plants().of(PlantType.WALLNUT).displayName());
    }

    @Test
    void zombieDisplayNameComesFromJsonConfig() {
        assertEquals("普通僵尸", TestFixture.zombies().of(ZombieType.BASIC).displayName());
        assertEquals("路障僵尸", TestFixture.zombies().of(ZombieType.CONEHEAD).displayName());
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
                SunProductionBehavior.PRODUCE_SUN,
                null,
                null,
                null,
                null));
    }

    @Test
    void sunflowerSpriteConfigComesFromJson() {
        PlantConfig sunflower = TestFixture.plants().of(PlantType.SUNFLOWER);

        assertEquals("sunflower", sunflower.spriteKey());
        assertEquals(18, sunflower.frameCount());
        assertEquals(6.0, sunflower.animationFps());
        assertEquals("sunflowerCard.png", sunflower.cardImage());
        assertNull(TestFixture.plants().of(PlantType.PEASHOOTER).spriteKey());
    }

    @Test
    void levelCatalogFindsLevelById() {
        assertEquals("1-1", TestFixture.level("1-1").id());
        assertEquals("1-2", TestFixture.level("1-2").id());
        assertThrows(IllegalArgumentException.class, () -> TestFixture.level("missing"));
    }

    @Test
    void levelConfigRejectsIncompleteDefinition() {
        assertThrows(IllegalArgumentException.class, () -> new LevelConfig(
                "",
                "missing id",
                100,
                List.of(PlantType.SUNFLOWER),
                TestFixture.level("1-1").waves()));
        assertThrows(IllegalArgumentException.class, () -> new LevelConfig(
                "empty-plants",
                "empty plants",
                100,
                List.of(),
                TestFixture.level("1-1").waves()));
        assertThrows(IllegalArgumentException.class, () -> new LevelConfig(
                "empty-waves",
                "empty waves",
                100,
                List.of(PlantType.SUNFLOWER),
                List.of()));
    }

    @Test
    void catalogsRejectMissingAndDuplicateEntries() {
        assertThrows(IllegalStateException.class, () -> new PlantCatalog(Map.of(
                PlantType.SUNFLOWER,
                TestFixture.plants().of(PlantType.SUNFLOWER))));

        LevelConfig duplicate = new LevelConfig(
                "duplicate",
                "duplicate",
                100,
                List.of(PlantType.SUNFLOWER),
                List.of(new ZombieWave(1, List.of(new ZombieSpawn(ZombieType.BASIC, 1, 1)))));
        assertThrows(IllegalStateException.class, () -> new LevelCatalog(
                List.of(duplicate, duplicate),
                TestFixture.plants(),
                TestFixture.zombies()));
    }
}
