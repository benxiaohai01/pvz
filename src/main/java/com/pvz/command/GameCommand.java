package com.pvz.command;

/**
 * 游戏命令（Command Pattern）：玩家操作被封装为可执行、可撤销的对象。
 */
public interface GameCommand {

    boolean canExecute();

    boolean execute();

    void undo();
}
