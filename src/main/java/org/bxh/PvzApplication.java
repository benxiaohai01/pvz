package org.bxh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class PvzApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 16 * 50, 9 * 50);


        Image backgroundImage = new Image(new FileInputStream("D:\\projects\\javafx\\pvz\\src\\main\\resources\\白天.jpg"));
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,    // 不重复
                BackgroundRepeat.NO_REPEAT,    // 不重复
                BackgroundPosition.CENTER,     // 居中
                new BackgroundSize(
                        BackgroundSize.AUTO,       // 宽度自动
                        BackgroundSize.AUTO,       // 高度自动
                        false,                     // 不包含
                        false,                     // 不包含
                        true,                      // 保持宽高比
                        true                       // 覆盖整个区域
                )
        );

        root.setBackground(new Background(background));


        stage.setScene(scene);
        stage.setTitle("Pvz");
        stage.show();

    }
}
