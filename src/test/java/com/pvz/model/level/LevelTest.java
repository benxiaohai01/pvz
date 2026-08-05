package com.pvz.model.level;

import com.pvz.config.LevelCatalog;
import com.pvz.model.entity.zombie.ZombieType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    void wavesActivateInOrderByTime() {
        Level level = new Level(LevelCatalog.LEVEL_1_1);

        assertFalse(level.isWaveActive());
        assertTrue(level.currentSpawn().isEmpty());

        level.advance(10);
        assertTrue(level.isWaveActive());
        Optional<ZombieSpawn> first = level.currentSpawn();
        assertTrue(first.isPresent());
        assertEquals(3, first.get().count());

        level.completeWave();
        assertFalse(level.isWaveActive()); // 第 2 波 20 秒才到

        level.advance(10);
        Optional<ZombieSpawn> second = level.currentSpawn();
        assertTrue(second.isPresent());
        assertEquals(5, second.get().count());
    }

    @Test
    void allWavesSpawnedAfterLastWave() {
        Level level = new Level(LevelCatalog.LEVEL_1_1);
        for (int i = 0; i < 3; i++) {
            level.advance(100);
            assertTrue(level.isWaveActive());
            level.completeWave();
        }
        assertTrue(level.allWavesSpawned());
        assertEquals(3, level.totalWaves());
        assertFalse(level.isWaveActive());
    }

    @Test
    void waveTracksAnnouncementAndSpawnProgress() {
        Level level = new Level(LevelCatalog.LEVEL_1_1);
        level.advance(10);

        assertFalse(level.isWaveAnnounced());
        level.announceWave();
        assertTrue(level.isWaveAnnounced());

        ZombieSpawn spawn = level.currentSpawn().orElseThrow();
        assertEquals(ZombieType.BASIC, spawn.type());
        assertEquals(0, level.spawnedInEntry());

        level.advanceSpawnTimer(4);
        assertTrue(level.spawnTimer() >= spawn.spawnInterval());
        level.resetSpawnTimer();
        level.consumeSpawn();
        assertEquals(1, level.spawnedInEntry());

        level.consumeSpawn();
        level.consumeSpawn();
        assertEquals(0, level.spawnedInEntry());
        assertTrue(level.currentSpawn().isEmpty());
    }

    @Test
    void mixedWaveAdvancesAcrossSpawnEntries() {
        Level level = new Level(LevelCatalog.LEVEL_1_2);
        level.advance(25);
        // 顺序波次：第 1 波完成后才轮到第 2 波
        level.completeWave();
        level.advance(25);
        assertTrue(level.isWaveActive());

        ZombieSpawn first = level.currentSpawn().orElseThrow();
        assertEquals(ZombieType.BASIC, first.type());
        assertEquals(4, first.count());

        for (int i = 0; i < 4; i++) {
            level.consumeSpawn();
        }
        ZombieSpawn second = level.currentSpawn().orElseThrow();
        assertEquals(ZombieType.CONEHEAD, second.type());
        assertEquals(3, second.count());
    }
}
