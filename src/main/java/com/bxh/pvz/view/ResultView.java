package com.bxh.pvz.view;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.event.GameResult;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 结算视图：YOU WIN / GAME OVER。
 */
public final class ResultView {

    private final BorderPane root;

    public ResultView(GameResult result, Runnable onBack) {
        root = new BorderPane();
        root.setPrefSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);

        boolean win = result == GameResult.WIN;
        root.setStyle(win
                ? "-fx-background-color: linear-gradient(to bottom, #FFF9C4, #FFD54F);"
                : "-fx-background-color: linear-gradient(to bottom, #4E342E, #1B0000);");

        VBox center = new VBox(22);
        center.setAlignment(Pos.CENTER);

        Label title = new Label(win ? "YOU WIN" : "GAME OVER");
        title.setStyle(win
                ? "-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #F57F17;"
                : "-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");

        Label subtitle = new Label(win ? "胜利！所有僵尸都被消灭了" : "僵尸吃掉了你的脑子");
        subtitle.setStyle(win
                ? "-fx-font-size: 24px; -fx-text-fill: #5D4037;"
                : "-fx-font-size: 24px; -fx-text-fill: #FFCDD2;");

        Button backButton = new Button("返回主菜单");
        backButton.setPrefSize(220, 46);
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FFC107;");
        backButton.setOnAction(e -> onBack.run());

        center.getChildren().addAll(title, subtitle, backButton);
        root.setCenter(center);
    }

    public Parent getRoot() {
        return root;
    }
}
