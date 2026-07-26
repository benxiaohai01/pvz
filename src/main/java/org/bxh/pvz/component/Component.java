package org.bxh.pvz.component;

/**
 * 组件标记接口 —— ECS 中的数据载体。
 * 组件不包含逻辑，仅持有数据。逻辑由 System 处理。
 */
public sealed interface Component
        permits TransformComponent,
                HealthComponent,
                MovementComponent,
                AttackComponent,
                RenderComponent {
}
