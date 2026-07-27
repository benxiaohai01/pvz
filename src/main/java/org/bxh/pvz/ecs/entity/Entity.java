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

    public void setActive(boolean active) {
        this.active = active;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        return Optional.ofNullable((T) components.get(type));
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    @SafeVarargs
    public final boolean hasComponents(Class<? extends Component>... types) {
        for (Class<? extends Component> type : types) {
            if (!components.containsKey(type)) return false;
        }
        return true;
    }

    public void removeComponent(Class<? extends Component> type) {
        components.remove(type);
    }
}
