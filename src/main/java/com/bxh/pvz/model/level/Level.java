package com.bxh.pvz.model.level;

import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.ZombieSpawn;
import com.bxh.pvz.config.ZombieType;
import com.bxh.pvz.config.ZombieWave;

import java.util.List;
import java.util.Optional;

/**
 * 关卡运行时状态：推进时间、当前波次、波内生成进度。
 * 生成状态机完整归属这里，服务层只消费每次帧更新的结果。
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
    /** 当前正在生成或等待开始的波次下标。 */
    private int currentWaveIndex;
    /** 本局已经推进的游戏秒数，用于判断波次开始时间。 */
    private double elapsed;
    /** 当前波次是否已经发布过开始事件。 */
    private boolean waveAnnounced;
    /** 距离当前生成条目上一次生成僵尸的秒数。 */
    private double timeSinceLastSpawn;
    /** 当前波次内正在生成的第几个条目。 */
    private int currentSpawnEntryIndex;
    /** 当前生成条目已经生成的僵尸数量。 */
    private int spawnedCountInCurrentEntry;

    public Level(LevelConfig config) {
        this.config = config;
    }

    public LevelConfig config() {
        return config;
    }

    public double elapsed() {
        return elapsed;
    }

    /**
     * 推进一次波次生成状态机：
     * 先判断当前波是否到时间，再公告波次、生成一个僵尸或切换到下一波。
     */
    public SpawnTick tickSpawn(double delta) {
        elapsed += delta;
        if (allWavesSpawned() || !isWaveActive()) {
            return SpawnTick.none();
        }

        boolean announcedThisTick = false;
        if (!waveAnnounced) {
            // 当前波第一次进入生成流程时立即发出公告。
            waveAnnounced = true;
            announcedThisTick = true;
            timeSinceLastSpawn = 0;
        }

        Optional<ZombieSpawn> currentSpawnOption = currentSpawn();
        if (currentSpawnOption.isEmpty()) {
            // 当前波所有生成条目都已耗尽，进入下一波并复位状态。
            completeWave();
            return new SpawnTick(announcedThisTick, null);
        }

        ZombieSpawn currentSpawnEntry = currentSpawnOption.orElseThrow();
        timeSinceLastSpawn += delta;
        if (spawnedCountInCurrentEntry == 0
                || timeSinceLastSpawn >= currentSpawnEntry.spawnInterval()) {
            // 条目首只僵尸立即生成，后续僵尸按配置间隔生成。
            timeSinceLastSpawn = 0;
            ZombieType spawnedZombieType = currentSpawnEntry.type();
            consumeSpawn();
            return new SpawnTick(announcedThisTick, spawnedZombieType);
        }
        return new SpawnTick(announcedThisTick, null);
    }

    /** 当前应激活的波次（顺序波次：上一波生成完才轮到下一波）。 */
    public Optional<ZombieWave> activeWave() {
        if (currentWaveIndex >= config.waves().size()) {
            return Optional.empty();
        }
        ZombieWave activeWave = config.waves().get(currentWaveIndex);
        return elapsed >= activeWave.startTime() ? Optional.of(activeWave) : Optional.empty();
    }

    public boolean isWaveActive() {
        return activeWave().isPresent();
    }

    public boolean allWavesSpawned() {
        return currentWaveIndex >= config.waves().size();
    }

    private Optional<ZombieSpawn> currentSpawn() {
        if (!isWaveActive()) {
            return Optional.empty();
        }
        List<ZombieSpawn> spawnEntries = config.waves().get(currentWaveIndex).spawns();
        return currentSpawnEntryIndex < spawnEntries.size()
                ? Optional.of(spawnEntries.get(currentSpawnEntryIndex))
                : Optional.empty();
    }

    /** 当前条目生成一只僵尸后推进计数，达到上限时切到下一个条目。 */
    private void consumeSpawn() {
        ZombieSpawn spawn = currentSpawn()
                .orElseThrow(() -> new IllegalStateException("当前没有可生成的条目"));
        spawnedCountInCurrentEntry++;
        if (spawnedCountInCurrentEntry >= spawn.count()) {
            currentSpawnEntryIndex++;
            spawnedCountInCurrentEntry = 0;
        }
    }

    /** 结束当前波并清空波内进度，使下一波从零开始生成。 */
    private void completeWave() {
        if (allWavesSpawned()) {
            return;
        }
        currentWaveIndex++;
        waveAnnounced = false;
        timeSinceLastSpawn = 0;
        currentSpawnEntryIndex = 0;
        spawnedCountInCurrentEntry = 0;
    }

    public int waveIndex() {
        return currentWaveIndex;
    }

    public int totalWaves() {
        return config.waves().size();
    }
}
