package com.bxh.pvz.service;

import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;

import java.util.List;

/**
 * 关卡服务：提供关卡列表并记录当前选中的关卡。
 */
public final class LevelService {

    private final LevelCatalog catalog;
    private LevelConfig current;

    public LevelService(LevelCatalog catalog) {
        this.catalog = catalog;
    }

    public List<LevelConfig> levels() {
        return catalog.levels();
    }

    public void selectLevel(String id) {
        current = catalog.byId(id);
    }

    public LevelConfig currentLevel() {
        if (current == null) {
            throw new IllegalStateException("尚未选择关卡");
        }
        return current;
    }
}
