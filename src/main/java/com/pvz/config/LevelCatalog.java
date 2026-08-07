package com.pvz.config;

import com.pvz.model.level.LevelConfig;

import java.util.List;
import java.util.Objects;

/**
 * 关卡配置注册表：数据来自 config/levels.json，启动时加载。
 */
public final class LevelCatalog {

    private LevelCatalog() {
    }

    public static final List<LevelConfig> LEVELS = ConfigLoader.loadLevels();

    /**
     * 按 id 查询关卡；未知 id 直接抛异常，让配置问题尽早暴露。
     **/
    public static LevelConfig byId(String id) {
        Objects.requireNonNull(id, "id");
        return LEVELS.stream()
                .filter(level -> level.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知关卡: " + id));
    }
}
