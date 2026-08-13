package com.bxh.pvz.model.level;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.ZombieType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    void wavesActivateInOrderByTime() {
        Level level = new Level(TestFixture.level("1-1"));

        assertFalse(level.isWaveActive());
        assertTrue(level.tickSpawn(1).isQuiet());

        Level.SpawnTick firstWave = level.tickSpawn(9);
        assertTrue(level.isWaveActive());
        assertTrue(firstWave.waveAnnounced());
        assertEquals(ZombieType.BASIC, firstWave.spawnedType());

        for (int i = 0; i < 2; i++) {
            assertEquals(ZombieType.BASIC, level.tickSpawn(4).spawnedType());
        }
        assertTrue(level.tickSpawn(0).isQuiet());
        assertFalse(level.isWaveActive());

        Level.SpawnTick secondWave = level.tickSpawn(10);
        assertTrue(secondWave.waveAnnounced());
        assertEquals(ZombieType.BASIC, secondWave.spawnedType());
    }

    @Test
    void allWavesSpawnedAfterLastWave() {
        Level level = new Level(TestFixture.level("1-1"));
        for (int i = 0; i < 100 && !level.allWavesSpawned(); i++) {
            level.tickSpawn(100);
        }
        assertTrue(level.allWavesSpawned());
        assertEquals(3, level.totalWaves());
        assertFalse(level.isWaveActive());
    }

    @Test
    void waveTracksAnnouncementAndSpawnProgress() {
        Level level = new Level(TestFixture.level("1-1"));
        Level.SpawnTick first = level.tickSpawn(10);
        assertTrue(first.waveAnnounced());
        assertEquals(ZombieType.BASIC, first.spawnedType());

        assertTrue(level.tickSpawn(3.9).isQuiet());
        assertEquals(ZombieType.BASIC, level.tickSpawn(0.1).spawnedType());
        assertTrue(level.tickSpawn(3.9).isQuiet());
        assertEquals(ZombieType.BASIC, level.tickSpawn(0.1).spawnedType());
    }

    @Test
    void mixedWaveAdvancesAcrossSpawnEntries() {
        Level level = new Level(TestFixture.level("1-2"));
        level.tickSpawn(8);
        for (int i = 0; i < 4; i++) {
            level.tickSpawn(4);
        }
        level.tickSpawn(0);

        Level.SpawnTick waveStart = level.tickSpawn(17);
        assertTrue(level.isWaveActive());
        assertTrue(waveStart.waveAnnounced());
        assertEquals(ZombieType.BASIC, waveStart.spawnedType());

        for (int i = 0; i < 3; i++) {
            assertEquals(ZombieType.BASIC, level.tickSpawn(3).spawnedType());
        }
        assertEquals(ZombieType.CONEHEAD, level.tickSpawn(0).spawnedType());
    }
}
