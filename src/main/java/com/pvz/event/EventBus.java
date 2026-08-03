package com.pvz.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件总线（Observer Pattern）：订阅与发布解耦。
 */
public final class EventBus {

    @FunctionalInterface
    public interface Subscriber {
        void onEvent(GameEvent event);
    }

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void publish(GameEvent event) {
        for (Subscriber subscriber : subscribers) {
            subscriber.onEvent(event);
        }
    }
}
