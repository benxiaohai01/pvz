package com.pvz.service;

import com.pvz.config.LevelCatalog;
import com.pvz.model.level.LevelConfig;

import java.util.List;

/**
 * 关卡服务：提供关卡列表并记录当前选中的关卡。
 */
public final class LevelService {

    private LevelConfig current;

    public List<LevelConfig> levels() {
        return LevelCatalog.LEVELS;
    }

    public void selectLevel(String id) {
        current = LevelCatalog.byId(id);
    }

    public LevelConfig currentLevel() {
        if (current == null) {
            throw new IllegalStateException("尚未选择关卡");
        }
        return current;
    }
}
