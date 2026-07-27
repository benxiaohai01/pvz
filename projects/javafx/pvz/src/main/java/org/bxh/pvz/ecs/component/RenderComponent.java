package org.bxh.pvz.ecs.component;

/**
 * 渲染组件 —— 外观描述，由 RenderSystem 消费。
 * 当前阶段使用简单几何形状，后续替换为精灵图纹理索引。
 */
public final class RenderComponent implements Component {

    /** 形状类型枚举 */
    public enum ShapeType { CIRCLE, RECT, ROUNDED_RECT }

    private final ShapeType shapeType;
    private final double width;
    private final double height;
    private final String colorHex;
    private boolean visible;

    public RenderComponent(ShapeType shapeType, double width, double height, String colorHex) {
        this.shapeType = shapeType;
        this.width = width;
        this.height = height;
        this.colorHex = colorHex;
        this.visible = true;
    }

    public ShapeType shapeType() { return shapeType; }
    public double width() { return width; }
    public double height() { return height; }
    public String colorHex() { return colorHex; }
    public boolean visible() { return visible; }

    public void setVisible(boolean visible) { this.visible = visible; }
}
