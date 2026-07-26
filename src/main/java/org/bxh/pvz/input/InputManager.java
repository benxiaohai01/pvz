package org.bxh.pvz.input;

import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.world.GridMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 输入管理器 —— 桥接 JavaFX 输入事件与游戏域事件。
 * 管理当前选中的植物类型和种植队列。
 */
public final class InputManager {

    private final GameConfig config;
    private final EventBus eventBus;
    private final GridMap gridMap;
    private String selectedPlantType;
    private final List<Runnable> pendingActions = new ArrayList<>();

    public InputManager(GameConfig config, EventBus eventBus, GridMap gridMap) {
        this.config = config;
        this.eventBus = eventBus;
        this.gridMap = gridMap;
    }

    /** 绑定到 JavaFX Scene */
    public void attachToScene(Scene scene) {
        scene.setOnMouseClicked(this::onMouseClicked);
        scene.setOnMouseMoved(this::onMouseMoved);
    }

    private void onMouseClicked(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) return;

        GridMap.GridCell cell = gridMap.screenToGrid(e.getX(), e.getY());
        if (cell == null) return;

        if (selectedPlantType != null) {
            // 有选中的植物 → 发布种植事件
            double wx = gridMap.cellToScreenX(cell.col());
            double wy = gridMap.cellToScreenY(cell.row());
            pendingActions.add(() -> eventBus.publish(
                    new GameEvent.PlantPlaced(UUID.randomUUID(), selectedPlantType, cell.row(), cell.col())));
        }
    }

    private void onMouseMoved(MouseEvent e) {
        // 预留：后续用于高亮悬停格
    }

    /** 选择植物类型（由 UI 按钮触发） */
    public void selectPlant(String plantType) {
        this.selectedPlantType = plantType;
    }

    public String selectedPlantType() {
        return selectedPlantType;
    }

    /** 每帧调用：消费待处理输入动作 */
    public void processPending() {
        for (Runnable action : pendingActions) {
            action.run();
        }
        pendingActions.clear();
    }
}
