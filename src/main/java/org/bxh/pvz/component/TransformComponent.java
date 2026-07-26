package org.bxh.pvz.component;

/**
 * 变换组件 —— 位置、旋转、缩放（record 不可变，偏向数据语义）。
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
