package com.bxh.pvz.renderer;

import com.bxh.pvz.config.ColorValue;
import javafx.scene.paint.Color;

/**
 * 配置颜色到 JavaFX 颜色的映射，未来替换图片资源时只需要修改这里。
 */
public final class RendererColors {

    private RendererColors() {
    }

    public static Color of(ColorValue value) {
        return Color.rgb(value.r(), value.g(), value.b());
    }
}
