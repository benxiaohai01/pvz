package com.bxh.pvz.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateManagerTest {

    @Test
    void followsLegalFlowAndNotifiesListeners() {
        GameStateManager manager = new GameStateManager();
        List<GameState> states = new ArrayList<>();
        manager.addListener(states::add);

        manager.transitionTo(GameState.LEVEL_SELECT);
        manager.transitionTo(GameState.PLANT_SELECT);
        manager.transitionTo(GameState.PLAYING);
        manager.transitionTo(GameState.WIN);

        assertEquals(List.of(GameState.LEVEL_SELECT, GameState.PLANT_SELECT, GameState.PLAYING, GameState.WIN), states);
        manager.transitionTo(GameState.MENU);
        assertEquals(GameState.MENU, manager.current());
    }

    @Test
    void rejectsIllegalTransitionWithoutChangingState() {
        GameStateManager manager = new GameStateManager();

        assertThrows(IllegalStateException.class, () -> manager.transitionTo(GameState.PLAYING));
        assertEquals(GameState.MENU, manager.current());
    }

    @Test
    void sameStateTransitionIsIdempotent() {
        GameStateManager manager = new GameStateManager();

        assertTrue(manager.transitionTo(GameState.MENU));
        assertEquals(GameState.MENU, manager.current());
    }

    @Test
    void startAtAllowsDebugLaunchAndKeepsNormalTransitionsValid() {
        GameStateManager manager = new GameStateManager();
        List<GameState> states = new ArrayList<>();
        manager.addListener(states::add);

        manager.startAt(GameState.PLAYING);

        assertEquals(GameState.PLAYING, manager.current());
        assertEquals(List.of(GameState.PLAYING), states);
        manager.transitionTo(GameState.WIN);
        assertEquals(GameState.WIN, manager.current());
        assertEquals(List.of(GameState.PLAYING, GameState.WIN), states);
    }

    @Test
    void startAtCanOnlyBeUsedOnce() {
        GameStateManager manager = new GameStateManager();
        manager.startAt(GameState.PLAYING);

        assertThrows(IllegalStateException.class, () -> manager.startAt(GameState.LOSE));
        assertEquals(GameState.PLAYING, manager.current());
    }
}
