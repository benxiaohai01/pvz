package org.bxh.pvz.scene;

import javafx.scene.input.MouseEvent;
import org.bxh.pvz.core.GameRenderer;

/**
 * 【设计模式：状态模式（State）—— 每个场景是一个状态】
 * 游戏场景契约。每个场景实现自己的 update/render/input。
 */
public sealed interface GameScene
        permits SceneManager.StartScene,
                SceneManager.LevelSelectScene,
                SceneManager.PlantSelectScene,
                SceneManager.PlayScene,
                SceneManager.GameOverScene {

    void onEnter(SceneContext ctx);
    void update(double dt);
    void render(GameRenderer renderer);
    void onMousePressed(MouseEvent e);
    void onMouseReleased(MouseEvent e);
    void onMouseDragged(MouseEvent e);
    void onExit();
}