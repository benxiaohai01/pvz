package org.bxh.pvz.bootstrap;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.core.GameRenderer;
import org.bxh.pvz.scene.SceneManager;

public final class GameApplication extends Application {
    private SceneManager sceneManager;
    private GameRenderer renderer;
    private AnimationTimer loop;

    @Override
    public void start(Stage stage) {
        var config = GameConfig.defaultConfig();
        var canvas = new Canvas(config.windowWidth(), config.windowHeight());
        var root = new StackPane(canvas);
        var scene = new Scene(root, config.windowWidth(), config.windowHeight());
        canvas.setFocusTraversable(true); canvas.requestFocus();
        renderer = new GameRenderer(canvas, config);
        sceneManager = new SceneManager(config);
        scene.setOnMousePressed(e -> sceneManager.onMousePressed(e));
        scene.setOnMouseReleased(e -> sceneManager.onMouseReleased(e));
        scene.setOnMouseDragged(e -> sceneManager.onMouseDragged(e));
        loop = new AnimationTimer() {
            private long last = System.nanoTime();
            @Override public void handle(long now) {
                double dt = (now - last) / 1_000_000_000.0; last = now;
                if (dt > 0.05) dt = 0.05;
                sceneManager.update(dt); sceneManager.render(renderer);
            }
        };
        loop.start();
        stage.setTitle(config.windowTitle()); stage.setScene(scene);
        stage.setResizable(false); stage.show();
    }
    @Override public void stop() { if (loop != null) loop.stop(); }
    public static void main(String[] args) { launch(args); }
}