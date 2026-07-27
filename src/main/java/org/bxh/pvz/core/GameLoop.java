package org.bxh.pvz.core;

import javafx.animation.AnimationTimer;

/**
 * 【设计模式：游戏循环模式（Game Loop）】
 * 对 JavaFX AnimationTimer 的薄封装。仅负责计时增量，所有逻辑委托给 Game。
 */
public final class GameLoop extends AnimationTimer {

    /** 最大帧间隔（秒），防止卡顿后的大步进 */
    private static final double MAX_DELTA = 0.05;

    private final Game game;
    private long lastNanos;

    public GameLoop(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
        lastNanos = System.nanoTime();
        super.start();
    }

    @Override
    public void handle(long nowNanos) {
        double delta = (nowNanos - lastNanos) / 1_000_000_000.0;
        lastNanos = nowNanos;

        if (delta > MAX_DELTA) {
            delta = MAX_DELTA;
        }

        game.update(delta);
        game.render();
    }
}
