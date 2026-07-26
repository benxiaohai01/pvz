package org.bxh.pvz.entity;

import org.bxh.pvz.component.*;

/**
 * 植物实体 —— 通过组合组件构造，不通过继承定义植物类型。
 * 不同植物类型的差异由组件参数 + 工厂方法体现。
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

    /** 豌豆射手工厂 */
    public static PlantEntity createPeashooter(int row, int col, double x, double y) {
        PlantEntity entity = new PlantEntity("peashooter", row, col);
        entity.addComponent(TransformComponent.at(x, y));
        entity.addComponent(new HealthComponent(100));
        entity.addComponent(new AttackComponent(20, 400, 1.5));
        entity.addComponent(new RenderComponent(
                RenderComponent.ShapeType.CIRCLE, 30, 30, "#4CAF50"));
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
