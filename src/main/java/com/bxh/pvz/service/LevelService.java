package com.bxh.pvz.service;

import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;

import java.util.List;

/**
 * 关卡服务：提供关卡列表并记录当前选中的关卡。
 */
public final class LevelService {

    private final LevelCatalog catalog;
    /** 玩家在关卡选择界面选中的关卡配置。 */
    private LevelConfig currentLevelConfig;

    public LevelService(LevelCatalog catalog) {
        this.catalog = catalog;
    }

    public List<LevelConfig> levels() {
        return catalog.levels();
    }

    public void selectLevel(String id) {
        currentLevelConfig = catalog.byId(id);
    }

    public LevelConfig currentLevel() {
        if (currentLevelConfig == null) {
            throw new IllegalStateException("尚未选择关卡");
        }
        return currentLevelConfig;
    }
}
