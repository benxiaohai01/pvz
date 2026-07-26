package org.bxh.pvz.core;

import javafx.animation.AnimationTimer;

/**
 * Thin wrapper around JavaFX {@link AnimationTimer}.
 * Owns nothing but the timing delta; all logic is delegated to {@link Game}.
 */
public final class GameLoop extends AnimationTimer {

    private static final double MAX_DELTA = 0.05; // cap at ~20 FPS floor

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
