package org.bxh.pvz.ecs.component;

/**
 * 【设计模式：值对象（Value Object）—— 不可变 record】
 * 变换组件 —— 位置、旋转、缩放。
 * 使用 Java record 表达不可变语义，修改时通过 withPosition 创建新实例。
 * 由 MovementSystem 消费并替换。
 */
public record TransformComponent(
        double x,
        double y,
        double rotation,
        double scaleX,
        double scaleY) implements Component {

    public static TransformComponent at(double x, double y) {
        return new TransformComponent(x, y, 0.0, 1.0, 1.0);
    }

    public TransformComponent withPosition(double x, double y) {
        return new TransformComponent(x, y, rotation, scaleX, scaleY);
    }
}
