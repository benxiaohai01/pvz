package org.bxh.pvz.ecs.component;

/**
 * 【设计模式：组件模式（Component Pattern）—— ECS 架构中的 "C"】
 * 组件标记接口，是 ECS 中的数据载体。组件不包含逻辑，仅持有数据。
 * 所有业务逻辑由 System 处理。
 */
public sealed interface Component
        permits TransformComponent,
                HealthComponent,
                MovementComponent,
                AttackComponent,
                RenderComponent {
}
