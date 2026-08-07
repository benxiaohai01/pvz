package com.pvz.service;

import com.pvz.config.LevelCatalog;
import com.pvz.event.EventBus;
import com.pvz.event.GameEvent;
import com.pvz.event.WaveSpawnEvent;
import com.pvz.factory.ZombieFactory;
import com.pvz.model.entity.zombie.ZombieType;
import com.pvz.model.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnServiceTest {

    @Test
    void waveSpawnsConfiguredZombieCount() {
        EventBus eventBus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        eventBus.subscribe(events::add);

        GameWorld world = new GameWorld(LevelCatalog.byId("1-1"));
        SpawnService spawnService = new SpawnService(new ZombieFactory(), eventBus);

        spawnService.update(world, 10); // 第 1 波开始，立即生成第 1 只
        assertEquals(1, world.zombies().size());

        spawnService.update(world, 4); // 第 2 只
        spawnService.update(world, 4); // 第 3 只，波次完成
        assertEquals(3, world.zombies().size());
        assertTrue(world.level().currentSpawn().isEmpty());

        long waveEvents = events.stream().filter(e -> e instanceof WaveSpawnEvent).count();
        assertEquals(1, waveEvents);
        WaveSpawnEvent wave = (WaveSpawnEvent) events.get(0);
        assertEquals(1, wave.waveIndex());
    }

    @Test
    void mixedWaveSpawnsMultipleZombieTypes() {
        EventBus eventBus = new EventBus();
        GameWorld world = new GameWorld(LevelCatalog.byId("1-3"));
        SpawnService spawnService = new SpawnService(new ZombieFactory(), eventBus);

        spawnService.update(world, 8); // 第 1 波开始，BASIC 第一只立即生成
        assertEquals(1, world.zombies().size());
        assertEquals(ZombieType.BASIC, world.zombies().get(0).config().type());

        for (int i = 0; i < 5; i++) {
            spawnService.update(world, 3.0); // BASIC 第 2~6 只
        }
        assertEquals(6, world.zombies().size());

        spawnService.update(world, 0); // 切换到 CONEHEAD 条目，第一只立即生成
        assertEquals(7, world.zombies().size());
        assertEquals(ZombieType.CONEHEAD, world.zombies().get(6).config().type());
    }
}
