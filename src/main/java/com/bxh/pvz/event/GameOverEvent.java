package com.bxh.pvz.event;

/**
 * 游戏结束事件（胜利或失败）。
 */
public record GameOverEvent(GameResult result) implements GameEvent {
}
