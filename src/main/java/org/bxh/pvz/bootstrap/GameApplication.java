package org.bxh.pvz.bootstrap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.core.Game;

/**
 * JavaFX application entry point. Builds the window, canvas, and
 * hands control to {@link Game}.
 */
public final class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        var config = GameConfig.defaultConfig();

        var canvas = new Canvas(config.windowWidth(), config.windowHeight());
        var root = new StackPane(canvas);
        var scene = new Scene(root, config.windowWidth(), config.windowHeight());

        // Prevent focus traversal from moving off the canvas
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        var game = new Game(canvas, config);

        stage.setTitle(config.windowTitle());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        game.start(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
