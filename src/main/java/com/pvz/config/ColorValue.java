package com.pvz.config;

/**
 * RGB 颜色值对象（Record），渲染层再映射为具体图形 API 的颜色。
 * 这样模型层不依赖 JavaFX。
 */
public record ColorValue(int r, int g, int b) {

    public static ColorValue of(String hex) {
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        return new ColorValue(
                Integer.parseInt(h.substring(0, 2), 16),
                Integer.parseInt(h.substring(2, 4), 16),
                Integer.parseInt(h.substring(4, 6), 16));
    }
}
