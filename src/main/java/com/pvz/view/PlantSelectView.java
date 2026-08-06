package com.pvz.view;

import com.pvz.config.GameConfig;
import com.pvz.config.PlantCatalog;
import com.pvz.config.PlantConfig;
import com.pvz.controller.PlantSelectController;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.renderer.RendererColors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.Map;

/**
 * 植物选择视图：下方植物卡片，点击加入顶部选择栏，最多 5 种。
 */
public final class PlantSelectView {

    private final BorderPane root;
    private final PlantSelectController controller;
    private final Map<PlantType, Card> cards = new EnumMap<>(PlantType.class);
    private final HBox selectedBar = new HBox(10);
    private final Button startButton = new Button("开始游戏");

    public PlantSelectView(PlantSelectController controller) {
        this.controller = controller;
        root = new BorderPane();
        root.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #D7CCC8, #A1887F);");

        VBox top = new VBox(12);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(16));

        Label title = new Label("选择植物（最多 " + GameConfig.MAX_SELECTED_PLANTS + " 种）");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #3E2723;");

        Label selectedCaption = new Label("已经选择:");
        selectedCaption.setStyle("-fx-font-size: 15px; -fx-text-fill: #4E342E;");
        selectedBar.setAlignment(Pos.CENTER);
        selectedBar.setMinHeight(52);
        selectedBar.setStyle("-fx-background-color: rgba(62,39,35,0.55); -fx-background-radius: 8;");

        top.getChildren().addAll(title, selectedCaption, selectedBar);
        root.setTop(top);

        FlowPane cardArea = new FlowPane(18, 18);
        cardArea.setAlignment(Pos.CENTER);
        cardArea.setPadding(new Insets(24));
        for (PlantType type : controller.availablePlants()) {
            Card card = new Card(type);
            card.setOnMouseClicked(e -> {
                controller.toggle(type);
                refresh();
            });
            cards.put(type, card);
            cardArea.getChildren().add(card);
        }
        root.setCenter(cardArea);

        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16));
        Button backButton = new Button("返回关卡选择");
        backButton.setPrefSize(160, 42);
        backButton.setStyle("-fx-font-size: 15px; -fx-background-color: #EFEBE9;");
        backButton.setOnAction(e -> controller.backToLevelSelect());

        startButton.setPrefSize(200, 46);
        startButton.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #FFC107;");
        startButton.setOnAction(e -> controller.startGame());
        bottom.getChildren().addAll(backButton, startButton);
        root.setBottom(bottom);

        refresh();
    }

    private void refresh() {
        selectedBar.getChildren().clear();
        for (PlantType type : controller.selectedPlants()) {
            Rectangle chip = new Rectangle(44, 34, RendererColors.of(PlantCatalog.of(type).color()));
            chip.setArcWidth(6);
            chip.setArcHeight(6);
            selectedBar.getChildren().add(chip);
        }
        startButton.setDisable(controller.selectedPlants().isEmpty());
        for (Map.Entry<PlantType, Card> entry : cards.entrySet()) {
            entry.getValue().setSelected(controller.isSelected(entry.getKey()));
        }
    }

    public Parent getRoot() {
        return root;
    }

    /** 植物卡片：颜色方块 + 名称 + 价格。 */
    private static final class Card extends VBox {

        private final Rectangle rect;

        Card(PlantType type) {
            PlantConfig config = PlantCatalog.of(type);
            setAlignment(Pos.CENTER);
            setSpacing(6);
            setPadding(new Insets(10));
            setStyle("-fx-background-color: #FFF8E1; -fx-background-radius: 10; -fx-border-color: #6D4C41; -fx-border-width: 2;");

            rect = new Rectangle(56, 52, RendererColors.of(config.color()));
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            Label name = new Label(config.displayName());
            name.setStyle("-fx-font-size: 15px; -fx-text-fill: #3E2723;");
            Label cost = new Label("☀ " + config.cost());
            cost.setStyle("-fx-font-size: 13px; -fx-text-fill: #6D4C41;");

            getChildren().addAll(rect, name, cost);
        }

        void setSelected(boolean selected) {
            setStyle(selected
                    ? "-fx-background-color: #FFE082; -fx-background-radius: 10; -fx-border-color: #FF8F00; -fx-border-width: 3;"
                    : "-fx-background-color: #FFF8E1; -fx-background-radius: 10; -fx-border-color: #6D4C41; -fx-border-width: 2;");
            rect.setEffect(selected ? new javafx.scene.effect.DropShadow(8, Color.web("#FF8F00")) : null);
        }
    }
}
