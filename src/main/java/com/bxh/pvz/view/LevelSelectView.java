package com.bxh.pvz.view;

import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.controller.LevelSelectController;
import com.bxh.pvz.controller.LevelOption;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 关卡选择视图：以按钮展示可进入的关卡。
 */
public final class LevelSelectView {

    private final BorderPane rootPane;

    public LevelSelectView(LevelSelectController controller) {
        rootPane = new BorderPane();
        rootPane.setPrefSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, #BCAAA4, #8D6E63);");

        VBox centerContent = new VBox(22);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(30));

        Label title = new Label("选择关卡");
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #3E2723;");
        centerContent.getChildren().add(title);

        for (LevelOption level : controller.levelOptions()) {
            Button button = new Button("[" + level.id() + "]  " + level.name());
            button.setPrefWidth(280);
            button.setPrefHeight(52);
            button.setStyle("-fx-font-size: 20px; -fx-background-color: #FFF8E1; -fx-text-fill: #3E2723;");
            button.setOnAction(event -> controller.selectLevel(level.id()));
            centerContent.getChildren().add(button);
        }

        Button backButton = new Button("返回主菜单");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(38);
        backButton.setStyle("-fx-font-size: 15px; -fx-background-color: #EFEBE9;");
        backButton.setOnAction(event -> controller.backToMenu());

        VBox bottom = new VBox(backButton);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(14));
        rootPane.setBottom(bottom);
        rootPane.setCenter(centerContent);
    }

    public Parent getRoot() {
        return rootPane;
    }
}
