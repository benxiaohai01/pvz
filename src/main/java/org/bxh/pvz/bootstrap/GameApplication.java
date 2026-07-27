package org.bxh.pvz.bootstrap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.core.Game;

/**
 * JavaFX 应用入口。构建窗口与画布，将控制权移交给 Game。
 */
public final class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        var config = GameConfig.defaultConfig();

        var canvas = new Canvas(config.windowWidth(), config.windowHeight());
        var root = new StackPane(canvas);
        var scene = new Scene(root, config.windowWidth(), config.windowHeight());

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
