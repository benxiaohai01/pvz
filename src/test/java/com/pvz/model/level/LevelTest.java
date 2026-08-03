package com.pvz.model.level;

import com.pvz.config.LevelCatalog;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    void wavesActivateInOrderByTime() {
        Level level = new Level(LevelCatalog.LEVEL_1_1);

        assertTrue(level.activeWave().isEmpty());

        level.advance(10);
        Optional<ZombieWave> first = level.activeWave();
        assertTrue(first.isPresent());
        assertEquals(3, first.get().count());

        level.advanceWave();
        assertTrue(level.activeWave().isEmpty()); // 第 2 波 20 秒才到

        level.advance(10);
        Optional<ZombieWave> second = level.activeWave();
        assertTrue(second.isPresent());
        assertEquals(5, second.get().count());
    }

    @Test
    void allWavesSpawnedAfterLastWave() {
        Level level = new Level(LevelCatalog.LEVEL_1_1);
        for (int i = 0; i < 3; i++) {
            level.advance(100);
            assertTrue(level.activeWave().isPresent());
            level.advanceWave();
        }
        assertTrue(level.allWavesSpawned());
        assertEquals(3, level.totalWaves());
        assertFalse(level.activeWave().isPresent());
    }
}
