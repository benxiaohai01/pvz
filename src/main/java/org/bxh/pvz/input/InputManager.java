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
import java.util.Optional;
import java.util.UUID;

/**
 * 【设计模式：适配器模式（Adapter）—— 将 JavaFX 输入事件适配为游戏域事件】
 * 输入管理器 —— 桥接 JavaFX 输入事件与游戏域事件。
 * 支持从顶部物品栏拖拽植物卡片到草坪种植。
 */
public final class InputManager {

    /** 卡片定义 —— 供 GameRenderer 绘制和点击检测 */
    public record PlantCard(String plantType, String label, double x, double y, double w, double h) {}

    private final GameConfig config;
    private final EventBus eventBus;
    private final GridMap gridMap;
    private final List<PlantCard> cards = new ArrayList<>();
    private final List<Runnable> pendingActions = new ArrayList<>();

    // 拖拽状态
    private boolean dragging;
    private double mouseX, mouseY;
    private String dragPlantType;

    public InputManager(GameConfig config, EventBus eventBus, GridMap gridMap) {
        this.config = config;
        this.eventBus = eventBus;
        this.gridMap = gridMap;
        initCards();
    }

    /** 初始化顶部物品栏植物卡片布局 */
    private void initCards() {
        int startX = 20, cy = 15, gap = 10, cw = 120, ch = 60;
        cards.add(new PlantCard("peashooter", "豌豆射手", startX, cy, cw, ch));
        // 后续扩展：向日葵、坚果墙等
    }

    public List<PlantCard> cards() { return cards; }
    public boolean dragging() { return dragging; }
    public double mouseX() { return mouseX; }
    public double mouseY() { return mouseY; }
    public String dragPlantType() { return dragPlantType; }

    /** 绑定到 JavaFX Scene */
    public void attachToScene(Scene scene) {
        scene.setOnMousePressed(this::onMousePressed);
        scene.setOnMouseDragged(this::onMouseDragged);
        scene.setOnMouseReleased(this::onMouseReleased);
    }

    private void onMousePressed(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) return;

        for (var card : cards) {
            if (e.getX() >= card.x() && e.getX() <= card.x() + card.w()
                    && e.getY() >= card.y() && e.getY() <= card.y() + card.h()) {
                dragging = true;
                dragPlantType = card.plantType();
                mouseX = e.getX();
                mouseY = e.getY();
                return;
            }
        }
    }

    private void onMouseDragged(MouseEvent e) {
        if (!dragging) return;
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void onMouseReleased(MouseEvent e) {
        if (!dragging) return;
        dragging = false;

        // 先捕获 plantType 到 final 变量，避免在 lambda 中引用被后续置 null 的字段
        final var plantType = dragPlantType;
        dragPlantType = null;

        Optional.ofNullable(gridMap.screenToGrid(e.getX(), e.getY())).ifPresent(cell -> {
            if (plantType != null) {
                pendingActions.add(() -> eventBus.publish(
                        new GameEvent.PlantPlaced(UUID.randomUUID(), plantType, cell.row(), cell.col())));
            }
        });
    }

    /** 每帧调用：消费待处理输入动作 */
    public void processPending() {
        pendingActions.forEach(Runnable::run);
        pendingActions.clear();
    }
}