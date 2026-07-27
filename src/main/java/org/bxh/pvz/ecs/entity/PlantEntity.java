package org.bxh.pvz.ecs.entity;

import org.bxh.pvz.ecs.component.AttackComponent;
import org.bxh.pvz.ecs.component.HealthComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;

/**
 * 【设计模式：工厂方法模式（Factory Method）—— 静态工厂创建不同植物类型】
 * 植物实体 —— 通过组合组件构造，不通过继承定义植物类型。
 */
public final class PlantEntity extends Entity {

    private final String plantType;
    private final int gridRow;
    private final int gridCol;

    private PlantEntity(String plantType, int gridRow, int gridCol) {
        this.plantType = plantType;
        this.gridRow = gridRow;
        this.gridCol = gridCol;
    }

    public String plantType() { return plantType; }
    public int gridRow() { return gridRow; }
    public int gridCol() { return gridCol; }

    /**
     * 豌豆射手 —— 竖立绿色长方体 24x44px。
     * 伤害15，冷却0.7s，射程350px（约4.4格）。
     * 数值设计：单发射手需前排阻挡才能安全输出，鼓励多种植物配合。
     */
    public static PlantEntity createPeashooter(int row, int col, double x, double y) {
        PlantEntity entity = new PlantEntity("peashooter", row, col);
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(100));
        entity.addComponent(new AttackComponent(15, 350, 0.7));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.RECT, 24, 44, "#4CAF50"));
        return entity;
    }

    /** 向日葵工厂 */
    public static PlantEntity createSunflower(int row, int col, double x, double y) {
        PlantEntity entity = new PlantEntity("sunflower", row, col);
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(80));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.CIRCLE, 36, 36, "#FFD700"));
        return entity;
    }

    /** 坚果墙工厂 */
    public static PlantEntity createWallNut(int row, int col, double x, double y) {
        PlantEntity entity = new PlantEntity("wallnut", row, col);
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(400));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.ROUNDED_RECT, 40, 40, "#8D6E63"));
        return entity;
    }
}