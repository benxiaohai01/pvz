package org.bxh.pvz.event;

/**
 * 【设计模式：密封类型层级（Sealed Type Hierarchy）—— 编译期穷举检查】
 * 游戏事件密封接口。所有游戏域事件类型在此集中声明，
 * 配合 pattern matching switch 做编译期穷举检查。
 */
public sealed interface GameEvent
        permits GameEvent.ZombieKilled,
                GameEvent.PlantPlaced,
                GameEvent.GameStarted,
                GameEvent.GameOver {

    record ZombieKilled(java.util.UUID zombieId, double x, double y) implements GameEvent {}

    record PlantPlaced(java.util.UUID plantId, String plantType, int row, int col) implements GameEvent {}

    record GameStarted() implements GameEvent {}

    record GameOver(boolean victory) implements GameEvent {}
}
