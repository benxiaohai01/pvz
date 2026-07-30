package org.bxh.pvz.scene;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.bxh.pvz.config.GameConfig;
import org.bxh.pvz.config.LevelConfig;
import org.bxh.pvz.config.PlantConfig;
import org.bxh.pvz.core.Game;
import org.bxh.pvz.core.GameRenderer;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;
import org.bxh.pvz.ecs.entity.PlantEntity;
import org.bxh.pvz.ecs.system.SunSystem;
import org.bxh.pvz.ecs.system.WaveSystem;
import org.bxh.pvz.ecs.system.GameOverSystem;
import org.bxh.pvz.event.EventBus;
import org.bxh.pvz.event.GameEvent;
import org.bxh.pvz.gameplay.LawnMowerEntity;
import org.bxh.pvz.gameplay.SunEntity;
import org.bxh.pvz.input.InputManager;
import org.bxh.pvz.world.GameWorld;
import org.bxh.pvz.world.GridMap;

import java.util.ArrayList;
import java.util.List;

public final class SceneManager {

    private final SceneContext ctx = new SceneContext();
    private GameScene currentScene;
    private final GameConfig config;
    GameWorld world;
    EventBus eventBus;
    SunSystem sunSystem;
    Game activeGame;

    public SceneManager(GameConfig config) {
        this.config = config;
        SceneManagerHolder.INSTANCE = this;
        switchTo(new StartScene());
    }

    public SceneContext ctx() { return ctx; }
    public void switchTo(GameScene next) {
        if (currentScene != null) currentScene.onExit();
        currentScene = next; next.onEnter(ctx);
    }
    public void update(double dt) { currentScene.update(dt); }
    public void render(GameRenderer renderer) { currentScene.render(renderer); }
    public void onMousePressed(MouseEvent e) { currentScene.onMousePressed(e); }
    public void onMouseReleased(MouseEvent e) { currentScene.onMouseReleased(e); }
    public void onMouseDragged(MouseEvent e) { currentScene.onMouseDragged(e); }

    static final class StartScene implements GameScene {
        boolean hoverStart;
        @Override public void onEnter(SceneContext ctx) {}
        @Override public void onExit() {}
        @Override public void update(double dt) {}
        @Override public void render(GameRenderer r) {
            var g = r.gc(); g.setFill(Color.web("#1a2a0f")); g.fillRect(0, 0, 1024, 768);
            g.setFill(Color.web("#c0e050")); g.setFont(Font.font("SansSerif", 52));
            g.fillText("Plants vs Zombies", 230, 250);
            double bx = 380, by = 420, bw = 260, bh = 60;
            g.setFill(hoverStart ? Color.web("#7a9b3a") : Color.web("#5a7b2a"));
            g.fillRoundRect(bx, by, bw, bh, 10, 10);
            g.setFill(Color.web("#c0caa0")); g.setFont(Font.font("SansSerif", 24));
            g.fillText("开始游戏", bx + 70, by + 40);
        }
        @Override public void onMousePressed(MouseEvent e) {
            double bx = 380, by = 420, bw = 260, bh = 60;
            if (e.getX() >= bx && e.getX() <= bx + bw && e.getY() >= by && e.getY() <= by + bh)
                SceneManagerHolder.INSTANCE.switchTo(new LevelSelectScene());
        }
        @Override public void onMouseReleased(MouseEvent e) {}
        @Override public void onMouseDragged(MouseEvent e) {}
    }

    static final class LevelSelectScene implements GameScene {
        @Override public void onEnter(SceneContext ctx) {}
        @Override public void onExit() {}
        @Override public void update(double dt) {}
        @Override public void render(GameRenderer r) {
            var g = r.gc(); g.setFill(Color.web("#1a2a0f")); g.fillRect(0, 0, 1024, 768);
            g.setFill(Color.web("#c0e050")); g.setFont(Font.font("SansSerif", 42));
            g.fillText("选择关卡", 370, 200);
            double bx = 380, by = 300, bw = 260, bh = 80;
            g.setFill(Color.web("#5a7b2a")); g.fillRoundRect(bx, by, bw, bh, 10, 10);
            g.setFill(Color.web("#c0caa0")); g.setFont(Font.font("SansSerif", 26));
            g.fillText("第一关", bx + 85, by + 50);
        }
        @Override public void onMousePressed(MouseEvent e) {
            double bx = 380, by = 300, bw = 260, bh = 80;
            if (e.getX() >= bx && e.getX() <= bx + bw && e.getY() >= by && e.getY() <= by + bh)
                SceneManagerHolder.INSTANCE.switchTo(new PlantSelectScene());
        }
        @Override public void onMouseReleased(MouseEvent e) {}
        @Override public void onMouseDragged(MouseEvent e) {}
    }

    static final class PlantSelectScene implements GameScene {
        final List<String> selected = new ArrayList<>();
        static final List<PlantCard> CARDS = List.of(
                new PlantCard("peashooter", "豌豆射手", 100, "#4CAF50"),
                new PlantCard("sunflower", "向日葵", 50, "#FFD700"),
                new PlantCard("wallnut", "坚果墙", 50, "#8D6E63"));
        @Override public void onEnter(SceneContext ctx) { selected.clear(); }
        @Override public void onExit() {}
        @Override public void update(double dt) {}
        @Override public void render(GameRenderer r) {
            var g = r.gc(); g.setFill(Color.web("#1a2a0f")); g.fillRect(0, 0, 1024, 768);
            g.setFill(Color.web("#c0e050")); g.setFont(Font.font("SansSerif", 36));
            g.fillText("选择植物 (至少2种)", 300, 120);
            for (int i = 0; i < CARDS.size(); i++) {
                var c = CARDS.get(i); double cx = 200 + i * 230, cy = 250;
                g.setFill(selected.contains(c.type) ? Color.web("#6a8b3a") : Color.web("#4a5a2a"));
                g.fillRoundRect(cx, cy, 190, 180, 10, 10);
                g.setFill(Color.web(c.color)); g.fillRect(cx + 35, cy + 30, 30, 50);
                g.setFill(Color.web("#c0caa0")); g.setFont(Font.font("SansSerif", 16));
                g.fillText(c.label, cx + 80, cy + 60);
                g.fillText("阳光:" + c.price, cx + 80, cy + 85);
            }
            double bx = 420, by = 520, bw = 200, bh = 55;
            g.setFill(selected.size() >= 2 ? Color.web("#5a7b2a") : Color.web("#3a4a1a"));
            g.fillRoundRect(bx, by, bw, bh, 10, 10);
            g.setFill(Color.web("#c0caa0")); g.setFont(Font.font("SansSerif", 22));
            g.fillText("开始战斗!", bx + 40, by + 38);
        }
        @Override public void onMousePressed(MouseEvent e) {
            for (int i = 0; i < CARDS.size(); i++) {
                var c = CARDS.get(i); double cx = 200 + i * 230, cy = 250;
                if (e.getX() >= cx && e.getX() <= cx + 190 && e.getY() >= cy && e.getY() <= cy + 180) {
                    if (selected.contains(c.type)) selected.remove(c.type);
                    else if (selected.size() < 6) selected.add(c.type);
                    return;
                }
            }
            double bx = 420, by = 520, bw = 200, bh = 55;
            if (e.getX() >= bx && e.getX() <= bx + bw && e.getY() >= by && e.getY() <= by + bh && selected.size() >= 2) {
                SceneManagerHolder.INSTANCE.ctx().selectedPlants = new ArrayList<>(selected);
                SceneManagerHolder.INSTANCE.switchTo(new PlayScene());
            }
        }
        @Override public void onMouseReleased(MouseEvent e) {}
        @Override public void onMouseDragged(MouseEvent e) {}
    }

    record PlantCard(String type, String label, int price, String color) {}

    static final class PlayScene implements GameScene {
        InputManager inputManager;
        RenderPlan renderPlan = RenderPlan.NORMAL;
        String resultText = "";
        double resultTimer;
        enum RenderPlan { NORMAL, VICTORY, DEFEAT }

        @Override public void onEnter(SceneContext ctx) {
            SceneManager self = SceneManagerHolder.INSTANCE;
            var lvlCfg = LevelConfig.level1();
            var gridMap = new GridMap(self.config);
            self.world = new GameWorld(gridMap);
            self.eventBus = new EventBus();
            self.sunSystem = new SunSystem(self.eventBus, (int) lvlCfg.initialSun());
            inputManager = new InputManager(self.config, self.eventBus, gridMap, self.sunSystem, ctx.selectedPlants);
            var waveSys = new WaveSystem(lvlCfg);
            var gameOver = new GameOverSystem(self.eventBus, waveSys);
            self.activeGame = new Game(self.config, self.world, self.eventBus,
                    inputManager, self.sunSystem, waveSys, gameOver);
            self.eventBus.subscribe(GameEvent.PlantPlaced.class, ev -> onPlantPlaced(ev, self, gridMap));
            for (int r = 0; r < gridMap.rows(); r++)
                self.world.spawnEntity(LawnMowerEntity.create(r, gridMap.offsetX() - 30, gridMap.cellToScreenY(r)));
            self.eventBus.subscribe(GameEvent.GameOver.class, ev -> {
                renderPlan = ev.victory() ? RenderPlan.VICTORY : RenderPlan.DEFEAT;
                resultText = ev.victory() ? "Level Complete!" : "僵尸吃掉了你的脑子!"; resultTimer = 3.0;
            });
            self.eventBus.subscribe(GameEvent.LevelComplete.class, ev -> {
                renderPlan = RenderPlan.VICTORY; resultText = "Level Complete!"; resultTimer = 3.0;
            });
        }

        static void onPlantPlaced(GameEvent.PlantPlaced ev, SceneManager self, GridMap gridMap) {
            if (ev.plantType() == null) return;
            var cfg = switch (ev.plantType()) {
                case "peashooter" -> PlantConfig.peashooter();
                case "sunflower" -> PlantConfig.sunflower();
                case "wallnut" -> PlantConfig.wallnut();
                default -> null;
            };
            if (cfg == null) return;
            self.world.spawnEntity(PlantEntity.create(cfg, ev.row(), ev.col(),
                    gridMap.cellToScreenX(ev.col()), gridMap.cellToScreenY(ev.row())));
        }

        @Override public void onExit() {}

        @Override public void update(double dt) {
            if (SceneManagerHolder.INSTANCE.activeGame != null)
                SceneManagerHolder.INSTANCE.activeGame.update(dt);
            if (resultTimer > 0 && renderPlan != RenderPlan.NORMAL) {
                resultTimer -= dt;
                if (resultTimer <= 0) {
                    SceneManager self = SceneManagerHolder.INSTANCE;
                    self.ctx().gameOverVictory = renderPlan == RenderPlan.VICTORY;
                    self.switchTo(new GameOverScene());
                }
            }
        }

        @Override public void render(GameRenderer r) {
            if (SceneManagerHolder.INSTANCE.activeGame != null)
                SceneManagerHolder.INSTANCE.activeGame.render(r);
            int sun = SceneManagerHolder.INSTANCE.sunSystem != null
                    ? SceneManagerHolder.INSTANCE.sunSystem.sunCount() : 0;
            r.drawSunCounter(sun);
            if (renderPlan != RenderPlan.NORMAL) {
                var g = r.gc(); g.setGlobalAlpha(0.6);
                g.setFill(renderPlan == RenderPlan.VICTORY ? Color.web("#2a4a0a") : Color.web("#4a0a0a"));
                g.fillRect(0, 0, 1024, 768); g.setGlobalAlpha(1.0);
                g.setFill(Color.WHITE); g.setFont(Font.font("SansSerif", 48));
                g.fillText(resultText, 280, 380);
            }
        }

        @Override public void onMousePressed(MouseEvent e) {
            SceneManager self = SceneManagerHolder.INSTANCE;
            // 1. 先检测阳光点击
            if (self.world != null && self.sunSystem != null) {
                for (Entity entity : self.world.entities()) {
                    if (!(entity instanceof SunEntity sun) || !sun.active()) continue;
                    var tf = sun.getComponent(TransformComponent.class);
                    if (tf.isEmpty()) continue;
                    double dx = e.getX() - tf.get().x();
                    double dy = e.getY() - tf.get().y();
                    if (dx * dx + dy * dy < 20 * 20) { // 20px 点击范围
                        self.sunSystem.addSun(sun.sunValue());
                        self.world.destroyEntity(sun);
                        return;
                    }
                }
            }
            // 2. 常规输入（植物卡片）
            if (inputManager != null) inputManager.onMousePressed(e);
        }
        @Override public void onMouseReleased(MouseEvent e) { if (inputManager != null) inputManager.onMouseReleased(e); }
        @Override public void onMouseDragged(MouseEvent e) { if (inputManager != null) inputManager.onMouseDragged(e); }
    }

    static final class GameOverScene implements GameScene {
        @Override public void onEnter(SceneContext ctx) {}
        @Override public void onExit() {}
        @Override public void update(double dt) {}
        @Override public void render(GameRenderer r) {
            var g = r.gc();
            boolean win = SceneManagerHolder.INSTANCE.ctx().gameOverVictory;
            g.setFill(win ? Color.web("#2a4a0a") : Color.web("#4a0a0a")); g.fillRect(0, 0, 1024, 768);
            g.setFill(Color.WHITE); g.setFont(Font.font("SansSerif", 48));
            g.fillText(win ? "Victory!" : "Game Over", 360, 300);
            double bx = 350, by = 450, bw = 320, bh = 55;
            g.setFill(Color.web("#5a7b2a")); g.fillRoundRect(bx, by, bw, bh, 10, 10);
            g.setFill(Color.web("#c0caa0")); g.setFont(Font.font("SansSerif", 22));
            g.fillText("返回主菜单", bx + 90, by + 38);
        }
        @Override public void onMousePressed(MouseEvent e) {
            double bx = 350, by = 450, bw = 320, bh = 55;
            if (e.getX() >= bx && e.getX() <= bx + bw && e.getY() >= by && e.getY() <= by + bh)
                SceneManagerHolder.INSTANCE.switchTo(new StartScene());
        }
        @Override public void onMouseReleased(MouseEvent e) {}
        @Override public void onMouseDragged(MouseEvent e) {}
    }

    public static final class SceneManagerHolder { public static SceneManager INSTANCE; }
}