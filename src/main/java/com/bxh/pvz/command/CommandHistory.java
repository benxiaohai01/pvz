package com.bxh.pvz.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Bounded undo stack for executed game commands.
 */
public final class CommandHistory {

    private final Deque<GameCommand> commands = new ArrayDeque<>();
    private final int limit;

    public CommandHistory(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit 必须大于 0");
        }
        this.limit = limit;
    }

    public void push(GameCommand command) {
        commands.push(command);
        if (commands.size() > limit) {
            commands.removeLast();
        }
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    public boolean undo() {
        if (commands.isEmpty()) {
            return false;
        }
        commands.pop().undo();
        return true;
    }
}
