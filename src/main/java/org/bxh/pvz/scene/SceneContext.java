package org.bxh.pvz.scene;

/**
 * 场景间共享上下文 —— 关卡选择结果、植物选择结果等。
 */
public final class SceneContext {
    public int selectedLevel = 1;
    public java.util.List<String> selectedPlants = java.util.List.of("peashooter", "sunflower");
    public boolean gameOverVictory;
}