package com.pvz.core;

import javafx.animation.AnimationTimer;

/**
 * 基于 JavaFX AnimationTimer 的游戏循环。
 *
 * 设计目标：
 * 1. 只负责“帧驱动”，具体游戏逻辑通过 {@link Listener} 回调，本类不知道任何玩法细节；
 * 2. 每帧先计算与上一帧的时间差 delta（秒），再按 update -> render 的顺序执行，
 *    让游戏逻辑和渲染都使用 delta 做帧率无关更新；
 * 3. start/stop 幂等，避免重复启动产生多个计时器或重复停止导致状态不一致；
 * 4. 回调运行在 JavaFX Application Thread 上，因此 update 与 render 之间没有线程竞争。
 *
 * 每帧流程：计算 delta -> 累加 elapsed -> 记录 lastDelta -> listener.update(delta) -> listener.render()。
 */
public final class GameLoop {

    /**
     * 游戏循环回调。
     * update 推进游戏逻辑（移动、攻击、波次等），render 绘制当前帧；
     * 两者分离后，逻辑层可以完全不依赖渲染层。
     */
    public interface Listener {
        /** @param deltaSeconds 本帧与上一帧的时间差（秒），第一帧为 0。 */
        void update(double deltaSeconds);

        void render();
    }

    /** 游戏逻辑回调，由组合根（GameEngine）在创建循环时注入。 */
    private final Listener listener;

    /** JavaFX 动画计时器，每个显示器刷新周期回调一次 handle，是循环的驱动源。 */
    private final AnimationTimer timer;

    /** 上一帧时间戳（纳秒）。0 表示还没有上一帧，用于让首帧 delta 为 0，避免启动瞬间画面跳跃。 */
    private long lastNanos;

    /** 运行状态标志，配合 start/stop 保证启停幂等。 */
    private boolean running;

    /** 本循环累计推进的游戏时间（秒），停止期间不增长。 */
    private double elapsed;

    /** 上一帧的时长（秒），可供渲染插值、调试或帧率统计使用。 */
    private double lastDelta;

    /**
     * 构造游戏循环：装配回调与 AnimationTimer。
     * 一帧的具体计算和通知顺序封装在下方匿名 handle 中。
     */
    public GameLoop(Listener listener) {
        this.listener = listener;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long nowNanos) {
                // 首帧没有“上一帧”，delta 置 0；后续用纳秒差换算成秒。
                double delta = lastNanos == 0L
                        ? 0.0
                        : (nowNanos - lastNanos) / 1_000_000_000.0;
                lastNanos = nowNanos;
                elapsed += delta;
                lastDelta = delta;
                listener.update(delta);
                listener.render();
            }
        };
    }

    /**
     * 启动循环。
     * 重复调用不会创建第二个计时器；重置 lastNanos 保证重启后的首帧 delta 为 0。
     */
    public void start() {
        if (running) {
            return;
        }
        lastNanos = 0L;
        timer.start();
        running = true;
    }

    /**
     * 停止循环。
     * 重复调用是安全的；停止后 elapsed 与 lastDelta 保留最后一次帧状态。
     */
    public void stop() {
        if (!running) {
            return;
        }
        timer.stop();
        running = false;
    }

    /** 当前是否处于运行状态。 */
    public boolean isRunning() {
        return running;
    }

    /** 自循环创建以来累计推进的游戏时间（秒），供动画进度、计时等读取。 */
    public double elapsedTime() {
        return elapsed;
    }

    /** 上一帧的时长（秒），供帧率统计或渲染插值使用。 */
    public double lastDelta() {
        return lastDelta;
    }
}
