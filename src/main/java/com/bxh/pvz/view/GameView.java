package com.bxh.pvz.view;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.UiConfig;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.controller.GameController;
import com.bxh.pvz.controller.MouseController;
import com.bxh.pvz.controller.PlantOption;
import com.bxh.pvz.event.EventBus;
import com.bxh.pvz.event.GameEvent;
import com.bxh.pvz.event.WaveSpawnEvent;
import com.bxh.pvz.renderer.GameRenderer;
import com.bxh.pvz.renderer.RendererColors;
import com.bxh.pvz.renderer.SpriteCatalog;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.EnumMap;
import java.util.Map;

/**
 * 游戏视图：完整背景作为底层，顶部商店栏覆盖其上，下方显示可交互的游戏对象画布。
 */
public final class GameView {

    /**
     * 拖拽数据格式：用于在植物卡片与游戏画布之间传递植物类型。
     */
    private static final DataFormat PLANT_TYPE_DATA_FORMAT = new DataFormat("application/x-pvz-plant-type");

    private final StackPane rootPane;
    /** 完整背景画布，覆盖整个窗口并位于最底层。 */
    private final Canvas backgroundCanvas;
    /** 游戏对象画布，位于顶部商店栏下方并随背景一起横向移动。 */
    private final Canvas gameplayCanvas;
    /** 承载背景和游戏对象画布的横向世界层，开局镜头通过平移该层完成。 */
    private final Pane worldLayer;
    private final GraphicsContext gameplayGraphicsContext;
    private final Label sunLabel;
    private final Label killLabel;
    private final Label waveBanner;
    private final GameRenderer renderer;
    private final SpriteCatalog sprites;
    private final GameController gameController;
    /** 植物类型与顶部拖拽卡片的映射。 */
    private final Map<PlantType, PlantCard> plantCards = new EnumMap<>(PlantType.class);
    /** 植物类型与卡片展示数据的映射，用于刷新价格和可用状态。 */
    private final Map<PlantType, PlantOption> plantOptionsByType = new EnumMap<>(PlantType.class);
    private final Button shovelButton;
    /** 波次提示剩余显示秒数。 */
    private double bannerRemaining;
    private final EventBus eventBus;
    private final EventBus.Subscriber eventSubscriber;
    /** 开局镜头是否正在播放，播放期间忽略种植、点击和铲子输入。 */
    private boolean introActive;
    /** 开局镜头是否已经启动，防止场景重入时重复播放。 */
    private boolean introStarted;
    /** 开局镜头动画，销毁视图时停止并释放其定时器。 */
    private SequentialTransition introTransition;

    public GameView(
            GameController controller,
            EventBus eventBus,
            GameRenderer renderer,
            MouseController mouseController,
            SpriteCatalog sprites) {
        this.renderer = renderer;
        this.sprites = sprites;
        this.eventBus = eventBus;
        this.gameController = controller;

        rootPane = new StackPane();
        rootPane.setPrefSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        rootPane.setMinSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        rootPane.setMaxSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);

        // 背景画布与游戏对象画布放在同一个世界层，镜头平移时保持两者同步。
        backgroundCanvas = new Canvas(gameWorldWidth(), UiConfig.WINDOW_HEIGHT);
        renderer.drawBackground(backgroundCanvas.getGraphicsContext2D());

        gameplayCanvas = new Canvas(gameWorldWidth(), UiConfig.CANVAS_HEIGHT);
        gameplayCanvas.setLayoutY(UiConfig.UI_HEIGHT);
        gameplayGraphicsContext = gameplayCanvas.getGraphicsContext2D();
        gameplayCanvas.setOnMouseClicked(event -> {
            if (!introActive) {
                mouseController.onCanvasClicked(event.getX(), event.getY());
            }
        });
        configurePlantDropTarget(mouseController);

        worldLayer = new Pane(backgroundCanvas, gameplayCanvas);
        worldLayer.setPrefSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        worldLayer.setMinSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        worldLayer.setMaxSize(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT);
        // 世界层比窗口宽，用窗口尺寸裁切，防止道路背景和对象画到窗口外。
        worldLayer.setClip(new Rectangle(UiConfig.WINDOW_WIDTH, UiConfig.WINDOW_HEIGHT));

        // 顶部商店栏：阳光数量固定在最左侧，随后是植物卡片和右侧状态按钮。
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 14, 8, 14));
        topBar.setPrefWidth(UiConfig.WINDOW_WIDTH);
        topBar.setMaxWidth(UiConfig.WINDOW_WIDTH);
        topBar.setPrefHeight(UiConfig.UI_HEIGHT);
        topBar.setMinHeight(UiConfig.UI_HEIGHT);
        topBar.setMaxHeight(UiConfig.UI_HEIGHT);
        topBar.setStyle("-fx-background-color: rgba(62,39,35,0.88);");

        // 阳光标签是第一个子节点，因此固定显示在顶部栏最左侧。
        sunLabel = new Label("☀ 0");
        sunLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD54F;");
        topBar.getChildren().add(sunLabel);

        // 根据本局可用植物创建可拖拽卡片。
        for (PlantOption option : controller.plantOptions()) {
            PlantType type = option.type();
            PlantCard card = new PlantCard(option);
            plantOptionsByType.put(type, option);
            plantCards.put(type, card);
            topBar.getChildren().add(card);
        }

        shovelButton = new Button("铲子");
        shovelButton.setPrefSize(76, 54);
        shovelButton.setStyle("-fx-font-size: 15px; -fx-background-color: #8D6E63; -fx-text-fill: white;");
        shovelButton.setOnAction(event -> {
            if (!introActive) {
                controller.toggleShovel();
            }
        });

        killLabel = new Label("击杀: 0");
        killLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFCCBC;");

        // 弹性空白区域吸收剩余宽度，把铲子和击杀数推到顶部栏最右侧。
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(spacer, shovelButton, killLabel);

        waveBanner = new Label();
        waveBanner.setVisible(false);
        waveBanner.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #FFF176;"
                + " -fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 10;"
                + " -fx-padding: 10 26 10 26;");

        // 先放背景世界层，再放商店栏，最后放波次提示，保证上层元素不会挤压背景。
        rootPane.getChildren().addAll(worldLayer, topBar, waveBanner);
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        StackPane.setAlignment(waveBanner, Pos.TOP_CENTER);
        StackPane.setMargin(waveBanner, new Insets(UiConfig.UI_HEIGHT + 16, 0, 0, 0));

        this.eventSubscriber = this::onEvent;
        eventBus.subscribe(eventSubscriber);

        // 先绘制对象首帧，让镜头动画开始前草坪内的植物等元素已经可见。
        renderer.drawWorld(gameplayGraphicsContext, gameController.world(), 0);
    }

    /**
     * 为游戏画布配置植物卡片拖拽接收逻辑。
     */
    private void configurePlantDropTarget(MouseController mouseController) {
        gameplayCanvas.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (!introActive && dragboard.hasContent(PLANT_TYPE_DATA_FORMAT)) {
                // 仅接受植物卡片拖拽，避免其他类型内容被误放入草坪。
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
            event.consume();
        });

        gameplayCanvas.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (introActive || !dragboard.hasContent(PLANT_TYPE_DATA_FORMAT)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            String draggedPlantTypeName = (String) dragboard.getContent(PLANT_TYPE_DATA_FORMAT);
            PlantType plantType = PlantType.valueOf(draggedPlantTypeName);
            // 松手事件使用场景坐标，转回画布坐标后交给控制器换算网格。
            Point2D canvasPoint = gameplayCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
            boolean placedSuccessfully = mouseController.onCanvasDropped(
                    plantType,
                    canvasPoint.getX(),
                    canvasPoint.getY());

            event.setDropCompleted(placedSuccessfully);
            event.consume();
        });
    }

    private void onEvent(GameEvent event) {
        switch (event) {
            case WaveSpawnEvent waveSpawnEvent -> {
                waveBanner.setText("第 " + waveSpawnEvent.waveIndex() + " / "
                        + waveSpawnEvent.totalWaves() + " 波僵尸来袭！");
                bannerRemaining = 2.8;
            }
            default -> {
                // 其余事件由引擎处理
            }
        }
    }

    /**
     * 播放开局镜头：先滑到右侧道路预览僵尸出生区域，再滑回草坪。
     * 动画结束后才启动游戏循环，避免预览阶段已经推进波次时间。
     */
    public void playIntro(Runnable onFinished) {
        if (introStarted) {
            return;
        }
        introStarted = true;

        double cameraTravel = backgroundCanvas.getWidth() - UiConfig.CANVAS_WIDTH;
        if (cameraTravel <= 0) {
            // 背景宽度不足一个视口时没有右侧道路可预览，直接开始游戏。
            worldLayer.setTranslateX(0);
            onFinished.run();
            return;
        }

        introActive = true;
        double roadOffsetX = -cameraTravel;

        TranslateTransition slideToRoad = new TranslateTransition(
                Duration.seconds(UiConfig.INTRO_FORWARD_SECONDS),
                worldLayer);
        slideToRoad.setFromX(0);
        slideToRoad.setToX(roadOffsetX);
        slideToRoad.setInterpolator(Interpolator.EASE_BOTH);

        PauseTransition roadPreview = new PauseTransition(
                Duration.seconds(UiConfig.INTRO_PREVIEW_SECONDS));

        TranslateTransition slideToLawn = new TranslateTransition(
                Duration.seconds(UiConfig.INTRO_RETURN_SECONDS),
                worldLayer);
        slideToLawn.setFromX(roadOffsetX);
        slideToLawn.setToX(0);
        slideToLawn.setInterpolator(Interpolator.EASE_BOTH);

        introTransition = new SequentialTransition(slideToRoad, roadPreview, slideToLawn);
        introTransition.setOnFinished(event -> {
            introTransition = null;
            introActive = false;
            worldLayer.setTranslateX(0);
            onFinished.run();
        });
        introTransition.play();
    }

    /**
     * 完整游戏画布宽度：优先取背景图片原始宽度，保证右侧道路不被压缩。
     */
    private double gameWorldWidth() {
        Image background = sprites.daytimeBackground();
        if (background != null && !background.isError()) {
            return Math.max(UiConfig.CANVAS_WIDTH, background.getWidth());
        }

        // 图片缺失时也要为僵尸出生点保留足够的右侧世界空间。
        return Math.max(
                UiConfig.CANVAS_WIDTH,
                GameConfig.SPAWN_X + GameConfig.ZOMBIE_HALF_WIDTH + 20);
    }

    public void refresh(GameController controller, double elapsed, double delta) {
        // 渲染游戏画面后同步阳光、击杀、冷却与铲子状态。
        renderer.drawWorld(gameplayGraphicsContext, controller.world(), elapsed);

        int currentSun = controller.world().sun();
        sunLabel.setText("☀ " + currentSun);
        killLabel.setText("击杀: " + controller.killCount());

        for (PlantType type : plantCards.keySet()) {
            PlantCard card = plantCards.get(type);
            double cooldownRemaining = controller.cooldownRemaining(type);
            boolean hasEnoughSun = currentSun >= plantOptionsByType.get(type).cost();
            card.setAffordable(hasEnoughSun && cooldownRemaining <= 0);
            card.setCooldown(cooldownRemaining > 0 ? String.format("%.1f", cooldownRemaining) : "");
        }

        // 铲子开启时切换为高亮样式，向玩家表明当前点击会铲除植物。
        shovelButton.setStyle(controller.shovelMode()
                ? "-fx-font-size: 15px; -fx-background-color: #FFB300; -fx-text-fill: #3E2723; -fx-border-color: white; -fx-border-width: 2;"
                : "-fx-font-size: 15px; -fx-background-color: #8D6E63; -fx-text-fill: white;");

        bannerRemaining = Math.max(0, bannerRemaining - delta);
        waveBanner.setVisible(bannerRemaining > 0);
    }

    public Parent getRoot() {
        return rootPane;
    }

    public void dispose() {
        if (introTransition != null) {
            introTransition.stop();
            introTransition = null;
        }
        eventBus.unsubscribe(eventSubscriber);
    }

    /** 顶部植物卡片，同时作为种植操作的拖拽源。 */
    private final class PlantCard extends StackPane {

        /** 卡片主图标，可能是图片，也可能是图片缺失时的颜色占位块。 */
        private final Node icon;
        /** 显示在卡片底部的冷却秒数标签。 */
        private final Label cooldownLabel;

        PlantCard(PlantOption option) {
            setAlignment(Pos.CENTER);
            setPadding(new Insets(2));
            setStyle("-fx-background-color: #6D4C41; -fx-background-radius: 8; -fx-border-color: #3E2723; -fx-border-width: 2;");

            // 有卡片图片时使用图片；没有对应资源时保留颜色占位块作为兜底。
            Image cardImage = sprites.cardOf(option.type());
            if (cardImage != null && !cardImage.isError()) {
                ImageView imageView = new ImageView(cardImage);
                imageView.setFitHeight(UiConfig.CARD_IMAGE_HEIGHT);
                imageView.setPreserveRatio(true); // 锁定卡片宽高比，避免图片拉伸变形
                imageView.setSmooth(true); // 开启平滑滤波，缩放后保持画面清晰
                icon = imageView;
            } else {
                Rectangle fallbackIcon = new Rectangle(42, 34, RendererColors.of(option.color()));
                fallbackIcon.setArcWidth(8);
                fallbackIcon.setArcHeight(8);
                icon = fallbackIcon;
            }

            cooldownLabel = new Label();
            cooldownLabel.setStyle("-fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #FFD54F;");

            VBox content = new VBox(2);
            content.setAlignment(Pos.CENTER);
            content.getChildren().addAll(icon, cooldownLabel);
            getChildren().add(content);

            configureDragSource(option.type());
        }

        /**
         * 把卡片注册为拖拽源；不可用时不会启动拖拽。
         */
        private void configureDragSource(PlantType plantType) {
            setOnDragDetected(event -> {
                // 开局镜头播放、冷却中或阳光不足时禁止开始拖拽。
                if (introActive || !gameController.canStartPlantDrag(plantType)) {
                    event.consume();
                    return;
                }

                Dragboard dragboard = startDragAndDrop(TransferMode.COPY);
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.put(PLANT_TYPE_DATA_FORMAT, plantType.name());
                dragboard.setContent(clipboardContent);

                Image cardImage = sprites.cardOf(plantType);
                if (cardImage != null && !cardImage.isError()) {
                    dragboard.setDragView(cardImage, cardImage.getWidth() / 2, cardImage.getHeight() / 2);
                }
                event.consume();
            });
        }

        /** 调整卡片透明度，表示阳光和冷却条件是否都满足。 */
        void setAffordable(boolean affordable) {
            setOpacity(affordable ? 1.0 : 0.45);
        }

        /** 更新底部冷却文字，无冷却时显示空字符串。 */
        void setCooldown(String text) {
            cooldownLabel.setText(text);
        }
    }
}
