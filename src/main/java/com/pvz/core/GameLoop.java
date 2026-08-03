package com.pvz.core;

import javafx.animation.AnimationTimer;

/**
 * 基于 JavaFX AnimationTimer 的游戏循环。
 * 每个帧先 update(delta) 更新游戏逻辑，再 render() 渲染画面。
 */
public final class GameLoop {

    public interface Listener {
        void update(double deltaSeconds);

        void render();
    }

    private final Listener listener;
    private final AnimationTimer timer;
    private long lastNanos;
    private boolean running;
    private double elapsed;
    private double lastDelta;

    public GameLoop(Listener listener) {
        this.listener = listener;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long nowNanos) {
                double delta = lastNanos == 0L
                        ? 0.0
                        : (nowNanos - lastNanos) / 1_000_000_000.0;
                lastNanos = nowNanos;
                elapsed += delta;
                lastDelta = delta;
                listener.update(delta);
                listener.render();
            }
        };
    }

    public void start() {
        if (running) {
            return;
        }
        lastNanos = 0L;
        timer.start();
        running = true;
    }

    public void stop() {
        if (!running) {
            return;
        }
        timer.stop();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public double elapsedTime() {
        return elapsed;
    }

    public double lastDelta() {
        return lastDelta;
    }
}
