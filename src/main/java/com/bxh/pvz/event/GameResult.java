package com.bxh.pvz.event;

/**
 * Domain result of a completed match. The presentation state machine maps this
 * to its own WIN or LOSE state.
 */
public enum GameResult {
    WIN,
    LOSE
}
