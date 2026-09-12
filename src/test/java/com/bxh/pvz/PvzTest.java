package com.bxh.pvz;

import com.bxh.pvz.launcher.GameApplication;
import javafx.application.Application;
import org.junit.jupiter.api.Test;

/**
 * @author 笨小孩
 * @since 2026/9/12 19:02
 **/
public class PvzTest {

    /**
     * 关卡启动测试
     */
    @Test
    public void levelStart() {
        // 直达游戏
        System.setProperty("pvz.dev.scene", "GAME");
        // 启动游戏
        Application.launch(GameApplication.class);
    }
}
