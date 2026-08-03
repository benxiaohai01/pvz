package com.pvz.event;

import com.pvz.core.GameState;

/**
 * 游戏结束事件（胜利或失败）。
 */
public record GameOverEvent(GameState result) implements GameEvent {
}
