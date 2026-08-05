package com.pvz.model.level;

import java.util.List;
import java.util.Optional;

/**
 * 关卡运行时状态：推进时间、当前波次、波内生成进度。
 * 生成状态归属关卡，服务层只读取并驱动，避免状态散落。
 */
public final class Level {

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

    public void advance(double delta) {
        elapsed += delta;
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

    /** 波次开始后标记公告，并进入可生成状态。 */
    public void announceWave() {
        waveAnnounced = true;
    }

    public boolean isWaveAnnounced() {
        return waveAnnounced;
    }

    /** 当前波中待生成的条目（条目耗尽后返回空，等待完成波次）。 */
    public Optional<ZombieSpawn> currentSpawn() {
        if (!isWaveActive()) {
            return Optional.empty();
        }
        List<ZombieSpawn> spawns = config.waves().get(waveIndex).spawns();
        return entryIndex < spawns.size()
                ? Optional.of(spawns.get(entryIndex))
                : Optional.empty();
    }

    /** 记录已生成一只当前条目僵尸，条目完成后自动切换下一个条目。 */
    public void consumeSpawn() {
        ZombieSpawn spawn = currentSpawn()
                .orElseThrow(() -> new IllegalStateException("当前没有可生成的条目"));
        spawnedInEntry++;
        if (spawnedInEntry >= spawn.count()) {
            entryIndex++;
            spawnedInEntry = 0;
        }
    }

    /** 当前条目已生成的数量（0 表示该条目尚未生成第一只）。 */
    public int spawnedInEntry() {
        return spawnedInEntry;
    }

    public double spawnTimer() {
        return spawnTimer;
    }

    public void advanceSpawnTimer(double delta) {
        spawnTimer += delta;
    }

    public void resetSpawnTimer() {
        spawnTimer = 0;
    }

    /** 当前波全部条目生成完毕，推进到下一波并重置波内状态。 */
    public void completeWave() {
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
