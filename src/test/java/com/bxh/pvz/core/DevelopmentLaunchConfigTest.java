package com.bxh.pvz.core;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.ZombieType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖调试直达参数解析、默认值和非法输入校验。
 */
class DevelopmentLaunchConfigTest {

    @Test
    void absentSceneUsesStandardLaunch() {
        DevelopmentLaunchConfig config = DevelopmentLaunchConfig.resolve(
                null,
                "1-2",
                "SUNFLOWER",
                "BASIC",
                TestFixture.levels());

        assertFalse(config.directToGame());
        assertEquals("", config.levelId());
        assertTrue(config.plantTypes().isEmpty());
        assertTrue(config.zombieTypes().isEmpty());
    }

    @Test
    void gameSceneFillsDefaultsFromTargetLevel() {
        DevelopmentLaunchConfig config = DevelopmentLaunchConfig.resolve(
                "GAME",
                null,
                null,
                null,
                TestFixture.levels());

        assertTrue(config.directToGame());
        assertEquals("1-1", config.levelId());
        assertEquals(
                List.of(PlantType.SUNFLOWER, PlantType.PEASHOOTER),
                config.plantTypes());
        assertEquals(
                List.of(ZombieType.BASIC, ZombieType.CONEHEAD),
                config.zombieTypes());
    }

    @Test
    void gameSceneAcceptsExplicitTypesAndIgnoresCase() {
        DevelopmentLaunchConfig config = DevelopmentLaunchConfig.resolve(
                " game ",
                "1-2",
                "wallnut, SUNFLOWER, wallnut",
                "conehead",
                TestFixture.levels());

        assertTrue(config.directToGame());
        assertEquals("1-2", config.levelId());
        assertEquals(List.of(PlantType.WALLNUT, PlantType.SUNFLOWER), config.plantTypes());
        assertEquals(List.of(ZombieType.CONEHEAD), config.zombieTypes());
    }

    @Test
    void rejectsUnknownLevelAndEnumValuesEarly() {
        assertThrows(IllegalArgumentException.class,
                () -> DevelopmentLaunchConfig.resolve(
                        "GAME",
                        "missing",
                        null,
                        null,
                        TestFixture.levels()));
        assertThrows(IllegalArgumentException.class,
                () -> DevelopmentLaunchConfig.resolve(
                        "GAME",
                        null,
                        "NOT_A_PLANT",
                        null,
                        TestFixture.levels()));
        assertThrows(IllegalArgumentException.class,
                () -> DevelopmentLaunchConfig.resolve(
                        "GAME",
                        null,
                        null,
                        "NOT_A_ZOMBIE",
                        TestFixture.levels()));
    }
}
