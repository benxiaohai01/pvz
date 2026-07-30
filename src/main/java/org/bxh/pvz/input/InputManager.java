package org.bxh.pvz.input;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.config.PlantConfig;
import org.bxh.pvz.ecs.system.SunSystem;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.gameplay.SunEntity;
import org.bxh.pvz.world.GameWorld;
import org.bxh.pvz.world.GridMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class InputManager {

    public record PlantCard(String plantType, String label, int price, double x, double y, double w, double h) {}

    private final GameConfig config;
    private final EventBus eventBus;
    private final GridMap gridMap;
    private final SunSystem sunSystem;
    private final Canvas canvas;
    private final GameWorld world;
    private final List<PlantCard> cards = new ArrayList<>();
    private final List<Runnable> pendingActions = new ArrayList<>();
    private boolean dragging;
    private double mouseX, mouseY;
    private String dragPlantType;

    public InputManager(GameConfig config, EventBus eventBus, GridMap gridMap, SunSystem sunSystem, List<String> selectedPlants) {
        this.config = config; this.eventBus = eventBus; this.gridMap = gridMap; this.sunSystem = sunSystem;
        this.canvas = new Canvas(config.windowWidth(), config.windowHeight());
        this.world = null;
        initCards(selectedPlants);
    }

    private void initCards(List<String> selectedPlants) {
        int startX = 20, cy = 15, gap = 10, cw = 120, ch = 60;
        for (int i = 0; i < selectedPlants.size(); i++) {
            var cfg = switch (selectedPlants.get(i)) {
                case "peashooter" -> PlantConfig.peashooter();
                case "sunflower" -> PlantConfig.sunflower();
                case "wallnut" -> PlantConfig.wallnut();
                default -> null;
            };
            if (cfg != null) cards.add(new PlantCard(cfg.type(), cfg.displayName(), cfg.price(), startX + i * (cw + gap), cy, cw, ch));
        }
    }

    public Canvas canvas() { return canvas; }
    public List<PlantCard> cards() { return cards; }
    public boolean dragging() { return dragging; }
    public double mouseX() { return mouseX; }
    public double mouseY() { return mouseY; }
    public String dragPlantType() { return dragPlantType; }

    public void setWorld(GameWorld world) { /* for future use */ }

    public void attachToScene(Scene scene) {
        scene.setOnMousePressed(this::onMousePressed);
        scene.setOnMouseDragged(this::onMouseDragged);
        scene.setOnMouseReleased(this::onMouseReleased);
    }

    public void onMousePressed(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) return;

        // 检测阳光收集（点击 SunEntity）
        // 通过 SceneManager 的 world 遍历检测
        // 这里简化：点击事件由场景层转发

        for (var card : cards) {
            if (e.getX() >= card.x() && e.getX() <= card.x() + card.w()
                    && e.getY() >= card.y() && e.getY() <= card.y() + card.h()) {
                dragging = true;
                dragPlantType = card.plantType();
                mouseX = e.getX(); mouseY = e.getY();
                return;
            }
        }
    }

    public void onMouseDragged(MouseEvent e) {
        if (!dragging) return;
        mouseX = e.getX(); mouseY = e.getY();
    }

    public void onMouseReleased(MouseEvent e) {
        if (!dragging) return;
        dragging = false;
        final var plantType = dragPlantType;
        dragPlantType = null;

        Optional.ofNullable(gridMap.screenToGrid(e.getX(), e.getY())).ifPresent(cell -> {
            if (plantType == null) return;
            int price = switch (plantType) {
                case "peashooter" -> PlantConfig.peashooter().price();
                case "sunflower" -> PlantConfig.sunflower().price();
                default -> 0;
            };
            if (sunSystem.spendSun(price)) {
                pendingActions.add(() -> eventBus.publish(new GameEvent.PlantPlaced(UUID.randomUUID(), plantType, cell.row(), cell.col())));
            }
        });
    }

    public void processPending() { pendingActions.forEach(Runnable::run); pendingActions.clear(); }
}