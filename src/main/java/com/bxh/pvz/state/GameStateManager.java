package com.bxh.pvz.state;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 顶层游戏流程的有限状态机，负责校验并执行状态迁移。
 */
public final class GameStateManager {

    private static final Map<GameState, Set<GameState>> TRANSITIONS = buildTransitions();

    private GameState current = GameState.MENU;
    /** 启动初始状态是否已经设置，确保该入口只能在状态机开始工作前调用一次。 */
    private boolean initialized;
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

    /**
     * 为调试直达等场景设置启动初始状态。
     *
     * <p>该方法是组合根在显示窗口前的一次性初始化入口，不属于正常状态迁移，
     * 因此不会把 {@code MENU -> PLAYING} 加入运行时合法迁移表。</p>
     */
    public void startAt(GameState initialState) {
        if (initialized) {
            throw new IllegalStateException("状态机只能设置一次启动初始状态");
        }
        current = Objects.requireNonNull(initialState, "启动初始状态不能为空");
        initialized = true;
        listeners.forEach(listener -> listener.accept(current));
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
