package org.bxh.pvz.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 【设计模式：观察者模式（Observer）—— 发布/订阅事件总线】
 * 轻量级事件总线。线程安全的发布/订阅实现。
 * 每帧由 Game.update() 调用 dispatch() 消费积压事件。
 */
public final class EventBus {

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<GameEvent>>> subscribers =
            new ConcurrentHashMap<>();
    private final Queue<GameEvent> pending = new ConcurrentLinkedQueue<>();

    /** 发布事件到待处理队列 */
    public void publish(GameEvent event) {
        pending.offer(event);
    }

    /** 订阅特定事件类型 */
    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<GameEvent>) handler);
    }

    /** 每帧调用：分发所有积压事件 */
    public void dispatch() {
        GameEvent event;
        while ((event = pending.poll()) != null) {
            final var evt = event;
            var handlers = subscribers.get(evt.getClass());
            if (handlers != null) {
                handlers.forEach(h -> h.accept(evt));
            }
        }
    }

    /** 清除所有订阅和待处理事件 */
    public void reset() {
        pending.clear();
        subscribers.clear();
    }
}