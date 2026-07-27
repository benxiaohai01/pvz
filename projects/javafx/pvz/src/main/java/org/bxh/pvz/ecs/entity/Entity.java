package org.bxh.pvz.ecs.entity;

import org.bxh.pvz.ecs.component.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【设计模式：实体模式（Entity Pattern）—— ECS 架构中的 "E"】
 * 实体是 ID 容器，仅持有组件集合，不包含业务逻辑。
 * 生命周期由 GameWorld 管理。
 */
public class Entity {

    private final UUID id;
    private final Map<Class<?>, Component> components;
    private boolean active;

    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new ConcurrentHashMap<>();
        this.active = true;
    }

    public UUID id() { return id; }
    public boolean active() { return active; }

    /** 标记实体为非活跃（下一帧由 GameWorld 移除） */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** 按类型获取组件 */
    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        return Optional.ofNullable((T) components.get(type));
    }

    /** 添加或替换组件 */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    /** 是否拥有指定组件类型 */
    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    /** 是否同时拥有多个组件类型（用于 System 的 Archetype 查询） */
    @SafeVarargs
    public final boolean hasComponents(Class<? extends Component>... types) {
        for (Class<? extends Component> type : types) {
            if (!components.containsKey(type)) return false;
        }
        return true;
    }

    /** 移除组件 */
    public void removeComponent(Class<? extends Component> type) {
        components.remove(type);
    }
}
