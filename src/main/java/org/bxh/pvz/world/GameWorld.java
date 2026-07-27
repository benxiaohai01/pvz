package org.bxh.pvz.world;

import org.bxh.pvz.ecs.entity.Entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 【设计模式：容器模式 —— 实体生命周期管理】
 * 游戏世界 —— 管理所有活跃实体的容器。
 * 提供实体的增删查，每帧由各 System 遍历处理。
 */
public final class GameWorld {

    private final GridMap gridMap;
    private final List<Entity> entities = new CopyOnWriteArrayList<>();
    private final List<Entity> pendingAdd = new CopyOnWriteArrayList<>();

    public GameWorld(GridMap gridMap) {
        this.gridMap = gridMap;
    }

    public GridMap gridMap() { return gridMap; }

    /** 获取所有活跃实体 */
    public List<Entity> entities() {
        return entities;
    }

    /** 注册实体（下一帧生效） */
    public void spawnEntity(Entity entity) {
        pendingAdd.add(entity);
    }

    /** 移除实体（下一帧生效） */
    public void destroyEntity(Entity entity) {
        entity.setActive(false);
    }

    /** 每帧开头调用：处理增删队列 */
    public void processPending() {
        entities.addAll(pendingAdd);
        pendingAdd.clear();
        entities.removeIf(e -> !e.active());
    }
}
