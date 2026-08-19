package com.bxh.pvz.state;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 顶层游戏流程的有限状态机，负责校验并执行状态迁移。
 */
public final class GameStateManager {

    private static final Map<GameState, Set<GameState>> TRANSITIONS = buildTransitions();

    private GameState current = GameState.MENU;
    private final List<Consumer<GameState>> listeners = new ArrayList<>();

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
        listeners.forEach(listener -> listener.accept(current));
        return true;
    }

    public void addListener(Consumer<GameState> listener) {
        listeners.add(listener);
    }

    private static Map<GameState, Set<GameState>> buildTransitions() {
        Map<GameState, Set<GameState>> transitionMap = new EnumMap<>(GameState.class);
        transitionMap.put(GameState.MENU, Set.of(GameState.LEVEL_SELECT));
        transitionMap.put(GameState.LEVEL_SELECT, Set.of(GameState.PLANT_SELECT, GameState.MENU));
        transitionMap.put(GameState.PLANT_SELECT, Set.of(GameState.PLAYING, GameState.LEVEL_SELECT));
        transitionMap.put(GameState.PLAYING, Set.of(GameState.WIN, GameState.LOSE));
        transitionMap.put(GameState.WIN, Set.of(GameState.MENU));
        transitionMap.put(GameState.LOSE, Set.of(GameState.MENU));
        return Map.copyOf(transitionMap);
    }
}
