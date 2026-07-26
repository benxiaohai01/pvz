package org.bxh.pvz.resource;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 纹理管理器 —— 运行时纹理缓存。
 * 基于 key 存取 JavaFX Image，为后续精灵系统做准备。
 */
public final class TextureManager {

    private final Map<String, Image> cache = new ConcurrentHashMap<>();
    private final AssetLoader assetLoader = new AssetLoader();

    /** 注册纹理 */
    public Image register(String key, Image image) {
        cache.put(key, image);
        return image;
    }

    /** 从 classpath 加载并缓存 */
    public Optional<Image> load(String key, String classpath) {
        Image cached = cache.get(key);
        if (cached != null) return Optional.of(cached);

        var stream = assetLoader.loadStream(classpath);
        if (stream == null) return Optional.empty();

        Image image = new Image(stream);
        cache.put(key, image);
        return Optional.of(image);
    }

    /** 获取已缓存纹理 */
    public Optional<Image> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void clear() {
        cache.clear();
    }
}
