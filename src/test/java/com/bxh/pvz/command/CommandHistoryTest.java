package com.bxh.pvz.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHistoryTest {

    @Test
    void undoesInReverseOrderAndDropsOldestBeyondLimit() {
        CommandHistory history = new CommandHistory(2);
        List<String> undoOrder = new ArrayList<>();
        history.push(command("one", undoOrder));
        history.push(command("two", undoOrder));
        history.push(command("three", undoOrder));

        assertTrue(history.undo());
        assertTrue(history.undo());
        assertFalse(history.undo());
        assertEquals(List.of("three", "two"), undoOrder);
    }

    @Test
    void rejectsInvalidLimit() {
        assertThrows(IllegalArgumentException.class, () -> new CommandHistory(0));
    }

    private static GameCommand command(String name, List<String> undoOrder) {
        return new GameCommand() {
            @Override
            public boolean canExecute() {
                return true;
            }

            @Override
            public boolean execute() {
                return true;
            }

            @Override
            public void undo() {
                undoOrder.add(name);
            }
        };
    }
}
