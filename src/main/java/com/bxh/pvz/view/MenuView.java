package com.bxh.pvz.view;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.controller.MenuController;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 主菜单视图。
 */
public final class MenuView {

    private final BorderPane root;

    public MenuView(MenuController controller) {
        root = new BorderPane();
        root.setPrefSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #A8D08D, #6B8E4F);");

        VBox center = new VBox(26);
        center.setAlignment(Pos.CENTER);

        Label title = new Label("植物大战僵尸");
        title.setStyle("-fx-font-size: 54px; -fx-font-weight: bold; -fx-text-fill: #3E2723;");

        Label subtitle = new Label("JavaFX 25 · 纯 OOP · MVC + 设计模式");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #5D4037;");

        Button startButton = new Button("开始游戏");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(46);
        startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #FFC107; -fx-text-fill: #3E2723;");
        startButton.setOnAction(e -> controller.startGame());

        Button exitButton = new Button("退出");
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(40);
        exitButton.setStyle("-fx-font-size: 16px; -fx-background-color: #EFEBE9; -fx-text-fill: #3E2723;");
        exitButton.setOnAction(e -> controller.exit());

        center.getChildren().addAll(title, subtitle, startButton, exitButton);
        root.setCenter(center);
    }

    public Parent getRoot() {
        return root;
    }
}
