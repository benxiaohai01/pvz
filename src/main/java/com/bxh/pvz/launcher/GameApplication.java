package com.bxh.pvz.launcher;

import com.bxh.pvz.core.GameEngine;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 游戏启动入口。只负责 JavaFX 启动与窗口创建，不包含任何游戏逻辑。
 */
public final class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        // 把 JavaFX 启动参数交给引擎解析，可配置调试直达页面。
        new GameEngine(stage, getParameters()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
