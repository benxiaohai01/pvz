package com.bxh.pvz.util;

/**
 * 二维向量 / 坐标值对象（Record）。
 */
public record Vector2(double x, double y) {

    public static final Vector2 ZERO = new Vector2(0, 0);

    public Vector2 plus(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 scaled(double factor) {
        return new Vector2(x * factor, y * factor);
    }

    public double distanceTo(Vector2 other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
