package org.bxh.pvz.gameplay;

import org.bxh.pvz.ecs.component.MovementComponent;
import org.bxh.pvz.ecs.component.RenderComponent;
import org.bxh.pvz.ecs.component.TransformComponent;
import org.bxh.pvz.ecs.entity.Entity;

public final class LawnMowerEntity extends Entity {
    public enum State { READY, RUNNING, USED }
    private final int row;
    private State state = State.READY;
    private LawnMowerEntity(int row) { this.row = row; }
    public int row() { return row; }
    public State state() { return state; }
    public void setState(State s) { this.state = s; }
    public static LawnMowerEntity create(int row, double x, double y) {
        LawnMowerEntity mower = new LawnMowerEntity(row);
        mower.addComponent(TransformComponent.at(x, y));
        var mv = new MovementComponent(400);
        mv.setVelocity(0, 0); // READY 状态不动，触发时才加速
        mower.addComponent(mv);
        mower.addComponent(new RenderComponent(RenderComponent.ShapeType.RECT, 48, 32, "#90A4AE"));
        return mower;
    }
}