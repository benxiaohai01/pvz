package com.pvz.config;

import com.pvz.model.level.LevelConfig;

import java.util.List;

/**
 * 关卡配置注册表：数据来自 config/levels.json，启动时加载。
 */
public final class LevelCatalog {

    private LevelCatalog() {
    }

    public static final List<LevelConfig> LEVELS = ConfigLoader.loadLevels();

    public static final LevelConfig LEVEL_1_1 = find("1-1");
    public static final LevelConfig LEVEL_1_2 = find("1-2");
    public static final LevelConfig LEVEL_1_3 = find("1-3");

    private static LevelConfig find(String id) {
        return LEVELS.stream()
                .filter(level -> level.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("配置中缺少关卡: " + id));
    }
}
