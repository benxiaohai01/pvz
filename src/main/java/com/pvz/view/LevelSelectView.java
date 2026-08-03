package com.pvz.view;

import com.pvz.config.GameConfig;
import com.pvz.controller.LevelSelectController;
import com.pvz.model.level.LevelConfig;
import com.pvz.service.LevelService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 关卡选择视图：类似 PvZ 的关卡按钮。
 */
public final class LevelSelectView {

    private final BorderPane root;

    public LevelSelectView(LevelSelectController controller, LevelService levelService) {
        root = new BorderPane();
        root.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #BCAAA4, #8D6E63);");

        VBox center = new VBox(22);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(30));

        Label title = new Label("选择关卡");
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #3E2723;");
        center.getChildren().add(title);

        for (LevelConfig level : levelService.levels()) {
            Button button = new Button("[" + level.id() + "]  " + level.name());
            button.setPrefWidth(280);
            button.setPrefHeight(52);
            button.setStyle("-fx-font-size: 20px; -fx-background-color: #FFF8E1; -fx-text-fill: #3E2723;");
            button.setOnAction(e -> controller.selectLevel(level.id()));
            center.getChildren().add(button);
        }

        Button backButton = new Button("返回主菜单");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(38);
        backButton.setStyle("-fx-font-size: 15px; -fx-background-color: #EFEBE9;");
        backButton.setOnAction(e -> controller.backToMenu());

        VBox bottom = new VBox(backButton);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(14));
        root.setBottom(bottom);
        root.setCenter(center);
    }

    public Parent getRoot() {
        return root;
    }
}
