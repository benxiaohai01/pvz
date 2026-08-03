package com.pvz.view;

import com.pvz.config.GameConfig;
import com.pvz.config.PlantCatalog;
import com.pvz.controller.GameController;
import com.pvz.controller.MouseController;
import com.pvz.event.EventBus;
import com.pvz.event.GameEvent;
import com.pvz.event.SunCollectedEvent;
import com.pvz.event.WaveSpawnEvent;
import com.pvz.event.ZombieDeathEvent;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.renderer.GameRenderer;
import com.pvz.renderer.RendererColors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏视图：顶部 UI（阳光/卡片/铲子/击杀数）+ 中央画布。
 */
public final class GameView {

    private final BorderPane root;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Label sunLabel;
    private final Label killLabel;
    private final Label waveBanner;
    private final GameRenderer renderer;
    private final Map<PlantType, PlantCard> cards = new EnumMap<>(PlantType.class);
    private final Button shovelButton;
    private double bannerRemaining;
    private int lastSun;
    private final EventBus eventBus;
    private final EventBus.Subscriber eventSubscriber;

    public GameView(
            GameController controller,
            List<PlantType> availablePlants,
            EventBus eventBus,
            GameRenderer renderer,
            MouseController mouseController) {
        this.renderer = renderer;
        this.eventBus = eventBus;

        root = new BorderPane();
        root.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 14, 10, 14));
        topBar.setMinHeight(GameConfig.UI_HEIGHT);
        topBar.setStyle("-fx-background-color: #4E342E;");

        sunLabel = new Label("☀ 0");
        sunLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD54F;");

        for (PlantType type : availablePlants) {
            PlantCard card = new PlantCard(type);
            card.setOnMouseClicked(e -> controller.selectPlant(type));
            cards.put(type, card);
            topBar.getChildren().add(card);
        }

        shovelButton = new Button("铲子");
        shovelButton.setPrefSize(76, 54);
        shovelButton.setStyle("-fx-font-size: 15px; -fx-background-color: #8D6E63; -fx-text-fill: white;");
        shovelButton.setOnAction(e -> controller.toggleShovel());

        killLabel = new Label("击杀: 0");
        killLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFCCBC;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(sunLabel, spacer, shovelButton, killLabel);
        root.setTop(topBar);

        canvas = new Canvas(GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        canvas.setOnMouseClicked(e -> mouseController.onCanvasClicked(e.getX(), e.getY()));

        waveBanner = new Label();
        waveBanner.setVisible(false);
        waveBanner.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #FFF176;"
                + " -fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 10;"
                + " -fx-padding: 10 26 10 26;");

        StackPane center = new StackPane(canvas, waveBanner);
        StackPane.setAlignment(waveBanner, Pos.TOP_CENTER);
        StackPane.setMargin(waveBanner, new Insets(16, 0, 0, 0));
        root.setCenter(center);

        this.eventSubscriber = this::onEvent;
        eventBus.subscribe(eventSubscriber);
    }

    private void onEvent(GameEvent event) {
        switch (event) {
            case SunCollectedEvent e -> {
                lastSun += e.amount();
                sunLabel.setText("☀ " + lastSun);
            }
            case WaveSpawnEvent e -> {
                waveBanner.setText("第 " + e.waveIndex() + " / " + e.totalWaves() + " 波僵尸来袭！");
                bannerRemaining = 2.8;
            }
            case ZombieDeathEvent e -> killLabel.setText("击杀: " + e.zombie().config().displayName() + " 已死亡");
            default -> {
                // 其余事件由引擎处理
            }
        }
    }

    public void refresh(GameController controller, double elapsed, double delta) {
        renderer.draw(gc, controller.world(), elapsed);

        lastSun = controller.world().sun();
        sunLabel.setText("☀ " + lastSun);
        killLabel.setText("击杀: " + controller.killCount());

        for (PlantType type : cards.keySet()) {
            PlantCard card = cards.get(type);
            double cooldown = controller.cooldownRemaining(type);
            boolean affordable = controller.world().sun() >= PlantCatalog.of(type).cost();
            card.setSelected(controller.selectedPlant() == type);
            card.setAffordable(affordable && cooldown <= 0);
            card.setCooldown(cooldown > 0 ? String.format("%.1f", cooldown) : "");
        }

        shovelButton.setStyle(controller.shovelMode()
                ? "-fx-font-size: 15px; -fx-background-color: #FFB300; -fx-text-fill: #3E2723; -fx-border-color: white; -fx-border-width: 2;"
                : "-fx-font-size: 15px; -fx-background-color: #8D6E63; -fx-text-fill: white;");

        bannerRemaining = Math.max(0, bannerRemaining - delta);
        waveBanner.setVisible(bannerRemaining > 0);
    }

    public Parent getRoot() {
        return root;
    }

    public void dispose() {
        eventBus.unsubscribe(eventSubscriber);
    }

    /** 顶部植物卡片按钮。 */
    private static final class PlantCard extends StackPane {

        private final Rectangle rect;
        private final Label nameLabel;
        private final Label cooldownLabel;

        PlantCard(PlantType type) {
            var config = PlantCatalog.of(type);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(4));
            setStyle("-fx-background-color: #6D4C41; -fx-background-radius: 8; -fx-border-color: #3E2723; -fx-border-width: 2;");

            rect = new Rectangle(42, 34, RendererColors.of(config.color()));
            rect.setArcWidth(8);
            rect.setArcHeight(8);

            nameLabel = new Label(config.displayName());
            nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
            cooldownLabel = new Label();
            cooldownLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFD54F;");

            VBox content = new VBox(2);
            content.setAlignment(Pos.CENTER);
            content.getChildren().addAll(rect, nameLabel, cooldownLabel);
            getChildren().add(content);
        }

        void setSelected(boolean selected) {
            setStyle(selected
                    ? "-fx-background-color: #FF8F00; -fx-background-radius: 8; -fx-border-color: white; -fx-border-width: 2;"
                    : "-fx-background-color: #6D4C41; -fx-background-radius: 8; -fx-border-color: #3E2723; -fx-border-width: 2;");
        }

        void setAffordable(boolean affordable) {
            setOpacity(affordable ? 1.0 : 0.45);
        }

        void setCooldown(String text) {
            cooldownLabel.setText(text);
        }
    }
}
