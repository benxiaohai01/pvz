package com.bxh.pvz.model.level;

import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.ZombieSpawn;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.config.ZombieWave;

import java.util.List;
import java.util.Optional;

/**
 * 关卡运行时状态：推进时间、当前波次、波内生成进度。
 * 生成状态机完整归属这里，服务层只消费每次 tick 的结果。
 */
public final class Level {

    public record SpawnTick(boolean waveAnnounced, ZombieType spawnedType) {

        private static final SpawnTick NONE = new SpawnTick(false, null);

        public static SpawnTick none() {
            return NONE;
        }

        public boolean isQuiet() {
            return !waveAnnounced && spawnedType == null;
        }
    }

    private final LevelConfig config;
    private int waveIndex;
    private double elapsed;
    private boolean waveAnnounced;
    private double spawnTimer;
    private int entryIndex;
    private int spawnedInEntry;

    public Level(LevelConfig config) {
        this.config = config;
    }

    public LevelConfig config() {
        return config;
    }

    public double elapsed() {
        return elapsed;
    }

    public SpawnTick tickSpawn(double delta) {
        elapsed += delta;
        if (allWavesSpawned() || !isWaveActive()) {
            return SpawnTick.none();
        }

        boolean announced = false;
        if (!waveAnnounced) {
            waveAnnounced = true;
            announced = true;
            spawnTimer = 0;
        }

        Optional<ZombieSpawn> spawn = currentSpawn();
        if (spawn.isEmpty()) {
            completeWave();
            return new SpawnTick(announced, null);
        }

        ZombieSpawn current = spawn.orElseThrow();
        spawnTimer += delta;
        if (spawnedInEntry == 0 || spawnTimer >= current.spawnInterval()) {
            spawnTimer = 0;
            ZombieType type = current.type();
            consumeSpawn();
            return new SpawnTick(announced, type);
        }
        return new SpawnTick(announced, null);
    }

    /** 当前应激活的波次（顺序波次：上一波生成完才轮到下一波）。 */
    public Optional<ZombieWave> activeWave() {
        if (waveIndex >= config.waves().size()) {
            return Optional.empty();
        }
        ZombieWave wave = config.waves().get(waveIndex);
        return elapsed >= wave.startTime() ? Optional.of(wave) : Optional.empty();
    }

    public boolean isWaveActive() {
        return activeWave().isPresent();
    }

    public boolean allWavesSpawned() {
        return waveIndex >= config.waves().size();
    }

    private Optional<ZombieSpawn> currentSpawn() {
        if (!isWaveActive()) {
            return Optional.empty();
        }
        List<ZombieSpawn> spawns = config.waves().get(waveIndex).spawns();
        return entryIndex < spawns.size()
                ? Optional.of(spawns.get(entryIndex))
                : Optional.empty();
    }

    private void consumeSpawn() {
        ZombieSpawn spawn = currentSpawn()
                .orElseThrow(() -> new IllegalStateException("当前没有可生成的条目"));
        spawnedInEntry++;
        if (spawnedInEntry >= spawn.count()) {
            entryIndex++;
            spawnedInEntry = 0;
        }
    }

    private void completeWave() {
        if (allWavesSpawned()) {
            return;
        }
        waveIndex++;
        waveAnnounced = false;
        spawnTimer = 0;
        entryIndex = 0;
        spawnedInEntry = 0;
    }

    public int waveIndex() {
        return waveIndex;
    }

    public int totalWaves() {
        return config.waves().size();
    }
}
