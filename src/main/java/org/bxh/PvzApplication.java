package org.bxh;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PvzApplication extends Application {

    /**
     * 场景高度
     */
    private static double sceneWidth = 900;
    private static double sceneHeight = (900 / 16) * 10.8;

    private static String boardStyle = "-fx-border-color: black; -fx-border-width: 2;";

    private BorderPane root; // 包含所有游戏元素的容器
    private Pane viewport;       // 视口，用于裁剪显示区域




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Group容器，可以将多个节点组合在一起统一操作（如旋转，缩放会统一操作）
//        Group root = new Group();

        root = new BorderPane();

        root.setPrefSize(sceneWidth, sceneHeight);

        // 添加背景图片
        ImageView background = createBackground();
        root.getChildren().add(background);

        // 创建顶部植物商店区域
        HBox plantShopArea = createPlantShopArea();
        root.setTop(plantShopArea);

        // 创建左侧小推车区域
        VBox vBox = createCarArea();
        root.setLeft(vBox);

        // 创建中间主游戏区域
        Pane mainGameArea = createMainGameArea();
        root.setCenter(mainGameArea);
//        root.getChildren().add(mainGameArea);

        // 创建右侧僵尸展示区域
        Pane zombieIntroArea = createZombieIntroArea();
//        root.getChildren().add(zombieIntroArea);
        root.setRight(zombieIntroArea);

        // 底部区域金币和进度区域
        HBox bottom = creatBottomArea();
        root.setBottom(bottom);

        // 创建视口（用于裁剪显示区域）
        viewport = new Pane(root);
        viewport.setClip(new Rectangle(sceneWidth, sceneHeight));

        // 创建场景
        Scene scene = new Scene(viewport, sceneWidth, sceneHeight);
        stage.setScene(scene);
        stage.setTitle("Pvz");
        stage.show();

        // 播放开场动画
//        playOpeningAnimation();

    }

    private HBox creatBottomArea() {
        HBox plantArea = new HBox();
        plantArea.setStyle(boardStyle);
        plantArea.setPrefSize(sceneWidth, sceneHeight / 19);
        return plantArea;
    }


    private HBox createPlantShopArea() {
        HBox plantArea = new HBox();
        plantArea.setStyle(boardStyle);
        plantArea.setPrefSize(sceneWidth, sceneHeight / 7.5);
//        plantArea.setPadding(Insets.EMPTY);  // 移除HBox的内边距
//        plantArea.setSpacing(0);             // 移除子节点之间的间距

//        ImageView shopImg = new ImageView();
//        shopImg.setFitWidth(446);
//        shopImg.setFitHeight(87);
//        shopImg.setStyle(boardStyle);
//        HBox.setMargin(shopImg, Insets.EMPTY);
//        shopImg.setImage(new Image("/植物商店.png"));
//        plantArea.getChildren().add(shopImg);

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("/植物商店.png"),
                BackgroundRepeat.NO_REPEAT, // 水平重复方式
                BackgroundRepeat.NO_REPEAT, // 垂直重复方式
                BackgroundPosition.DEFAULT, // 图片位置
                new BackgroundSize(// 背景尺寸
                        446,  // 宽度
                        87,    // 高度
                        false,// 宽度是否为百分比（即上面设置的宽高）
                        false, // 高度是否为百分比
                        true,// 是否包含（保持宽高比，会保持图片原来比例）
                        false)  // 是否覆盖（保持宽高比） true：图片会被拉伸按照容器的比例，可能会被裁剪
        );
        plantArea.setBackground(new Background(backgroundImage));





        return plantArea;
    }

    private VBox createCarArea() {
        VBox carArea = new VBox();
        carArea.setPrefSize(sceneWidth / 12, sceneHeight );
        carArea.setStyle(boardStyle);
        return carArea;
    }


    private ImageView createBackground() {
        // 创建背景图片
        ImageView background = new ImageView();
        background.setImage(new Image("/img.png"));
        background.setLayoutX(-175);
        background.setLayoutY(0);
        background.setFitHeight(sceneHeight);
        return background;
    }


    private Pane createMainGameArea() {
        Pane gameArea = new Pane();
        gameArea.setPrefSize(sceneWidth / 1.5, sceneHeight);
        gameArea.setStyle(boardStyle);
        // 添加草坪网格（5行9列）
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                Rectangle cell = new Rectangle(82, 94);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(1);
                cell.setLayoutX(col * 82);
                cell.setLayoutY(row * 94);
//                gameArea.getChildren().add(cell);
            }
        }


        return gameArea;
    }


    private Pane createZombieIntroArea() {
        Pane introArea = new Pane();
        introArea.setPrefSize(sceneWidth / 10, sceneHeight);
        introArea.setStyle(boardStyle);

        // 添加僵尸标题
        Text zombieTitle = new Text("僵尸大军来袭！");
        zombieTitle.setFont(Font.font("Arial", 36));
        zombieTitle.setFill(Color.RED);
        zombieTitle.setLayoutX(250 / 2);
        zombieTitle.setLayoutY(100 / 2);
        introArea.getChildren().add(zombieTitle);

        // 添加简单的僵尸表示（用红色矩形代替）
        for (int i = 0; i < 3; i++) {
            Rectangle zombie = new Rectangle(80, 120);
            zombie.setFill(Color.RED);
            zombie.setStroke(Color.DARKRED);
            zombie.setStrokeWidth(2);
            zombie.setLayoutX((200 + i * 150)/ 2);
            zombie.setLayoutY(200);
            introArea.getChildren().add(zombie);

            // 添加僵尸标签
            Text zombieText = new Text("僵尸" + (i + 1));
            zombieText.setFont(Font.font("Arial", 16));
            zombieText.setFill(Color.WHITE);
            zombieText.setLayoutX((210 + i * 150 )/ 2);
            zombieText.setLayoutY(280);
            introArea.getChildren().add(zombieText);
        }

        return introArea;
    }


    private void playOpeningAnimation() {
        // 1. 初始位置：显示右侧僵尸区域
        root.setTranslateX(-sceneWidth / 10 * 4);

        // 2. 延迟后开始移动动画
        PauseTransition initialPause = new PauseTransition(Duration.seconds(1));
        initialPause.setOnFinished(e -> {
            // 显示警告文本
            Text warningText = new Text("警告：僵尸接近中！");
            warningText.setFont(Font.font("Arial", 48));
            warningText.setFill(Color.RED);
            warningText.setLayoutX(sceneWidth); // 在右侧区域显示
            warningText.setLayoutY(sceneHeight / 3);
            root.getChildren().add(warningText);

            // 警告文本动画，创建一个持续0.5秒的淡入动画，应用于warningText标签
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), warningText);
            fadeIn.setFromValue(0); // 从完全透明开始
            fadeIn.setToValue(1); // 到完全不透明结束

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), warningText);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(1.5));

            // 设置动画结束后的操作
            fadeIn.setOnFinished(e2 -> fadeOut.play());
            fadeIn.play(); // 开始播放动画

            // 3. 移动回主游戏区域
            PauseTransition moveDelay = new PauseTransition(Duration.seconds(2));
            moveDelay.setOnFinished(e2 -> {
                // 创建移动动画
                TranslateTransition moveBack = new TranslateTransition(Duration.seconds(2), root);
                moveBack.setToX(0);
                moveBack.setInterpolator(Interpolator.EASE_BOTH);
                moveBack.play();

                // 移动完成后移除警告文本
                moveBack.setOnFinished(e3 -> {
                    root.getChildren().remove(warningText);
                    System.out.println("开场动画结束，游戏开始！");
                });
            });
            moveDelay.play();
        });
        initialPause.play();
    }

}
