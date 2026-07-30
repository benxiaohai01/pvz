package org.bxh.pvz.config;

import org.bxh.pvz.ecs.component.RenderComponent;

public record PlantConfig(String type, String displayName, int price, double maxHealth,
        double damage, double attackRange, double attackCooldown, double sunInterval,
        String colorHex, double width, double height, RenderComponent.ShapeType shapeType) {
    public static PlantConfig peashooter() {
        return new PlantConfig("peashooter", "豌豆射手", 100, 100, 15, 350, 0.7, 0, "#4CAF50", 24, 44, RenderComponent.ShapeType.RECT);
    }
    public static PlantConfig sunflower() {
        return new PlantConfig("sunflower", "向日葵", 50, 80, 0, 0, 0, 6.0, "#FFD700", 36, 36, RenderComponent.ShapeType.CIRCLE);
    }
    public static PlantConfig wallnut() {
        return new PlantConfig("wallnut", "坚果墙", 50, 400, 0, 0, 0, 0, "#8D6E63", 40, 40, RenderComponent.ShapeType.ROUNDED_RECT);
    }
}