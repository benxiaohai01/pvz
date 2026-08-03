package com.pvz.core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 游戏状态机：定义状态之间的合法迁移并通知监听者。
 * 使用 State Pattern 约束游戏流程，避免任意跳转。
 */
public final class GameStateManager {

    private static final Map<GameState, Set<GameState>> TRANSITIONS = buildTransitions();

    private GameState current = GameState.MENU;
    private final List<Consumer<GameState>> listeners = new java.util.ArrayList<>();

    public GameState current() {
        return current;
    }

    public boolean transitionTo(GameState next) {
        if (next == current) {
            return true;
        }
        if (!TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            throw new IllegalStateException("非法状态迁移: " + current + " -> " + next);
        }
        current = next;
        listeners.forEach(l -> l.accept(current));
        return true;
    }

    public void addListener(Consumer<GameState> listener) {
        listeners.add(listener);
    }

    private static Map<GameState, Set<GameState>> buildTransitions() {
        Map<GameState, Set<GameState>> map = new EnumMap<>(GameState.class);
        map.put(GameState.MENU, Set.of(GameState.LEVEL_SELECT));
        map.put(GameState.LEVEL_SELECT, Set.of(GameState.PLANT_SELECT, GameState.MENU));
        map.put(GameState.PLANT_SELECT, Set.of(GameState.PLAYING, GameState.LEVEL_SELECT));
        map.put(GameState.PLAYING, Set.of(GameState.WIN, GameState.LOSE));
        map.put(GameState.WIN, Set.of(GameState.MENU));
        map.put(GameState.LOSE, Set.of(GameState.MENU));
        return Map.copyOf(map);
    }
}
