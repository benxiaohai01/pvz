package org.bxh.pvz.ecs.component;

/**
 * 移动组件 —— 速度与方向，由 MovementSystem 消费。
 * 默认向左移动（僵尸朝向）。
 */
public final class MovementComponent implements Component {

    private double velocityX;
    private double velocityY;
    private double speed;

    public MovementComponent(double speed) {
        this.speed = speed;
        this.velocityX = -speed;
        this.velocityY = 0;
    }

    public double velocityX() { return velocityX; }
    public double velocityY() { return velocityY; }
    public double speed() { return speed; }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
