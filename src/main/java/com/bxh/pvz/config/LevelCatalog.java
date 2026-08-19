package com.bxh.pvz.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 关卡配置注册表：数据来自 config/levels.json，启动时加载。
 */
public final class LevelCatalog {

    private final List<LevelConfig> levels;
    private final Map<String, LevelConfig> byId;

    public LevelCatalog(PlantCatalog plants, ZombieCatalog zombies) {
        this(ConfigLoader.loadLevels(), plants, zombies);
    }

    LevelCatalog(List<LevelConfig> levels, PlantCatalog plants, ZombieCatalog zombies) {
        this.levels = List.copyOf(levels);
        this.byId = this.levels.stream().collect(Collectors.toUnmodifiableMap(
                LevelConfig::id,
                Function.identity(),
                (left, right) -> {
                    throw new IllegalStateException("重复关卡 id: " + left.id());
                }));
        validateReferences(plants, zombies);
    }

    public List<LevelConfig> levels() {
        return levels;
    }

    /**
     * 按编号查询关卡；未知编号直接抛异常，让配置问题尽早暴露。
     **/
    public LevelConfig byId(String id) {
        Objects.requireNonNull(id, "id");
        LevelConfig level = byId.get(id);
        if (level == null) {
            throw new IllegalArgumentException("未知关卡: " + id);
        }
        return level;
    }

    private void validateReferences(PlantCatalog plants, ZombieCatalog zombies) {
        for (LevelConfig level : levels) {
            for (PlantType type : level.availablePlants()) {
                plants.of(type);
            }
            for (ZombieWave wave : level.waves()) {
                for (ZombieSpawn spawn : wave.spawns()) {
                    zombies.of(spawn.type());
                }
            }
        }
    }
}
