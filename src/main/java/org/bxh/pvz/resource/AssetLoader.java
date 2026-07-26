package org.bxh.pvz.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * 资源加载器 —— 提供统一的资源发现与加载入口。
 * 目前为同步加载；后续可扩展异步加载 + 缓存。
 */
public final class AssetLoader {

    private static final Logger log = Logger.getLogger(AssetLoader.class.getName());

    /** 从 classpath 加载资源流 */
    public InputStream loadStream(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            log.warning("Resource not found on classpath: " + path);
        }
        return stream;
    }

    /** 从文件系统加载图片（为后续精灵扩展做准备） */
    public BufferedImage loadImage(Path filePath) {
        try (InputStream is = Files.newInputStream(filePath)) {
            return ImageIO.read(is);
        } catch (Exception e) {
            log.severe("Failed to load image: " + filePath + " - " + e.getMessage());
            return null;
        }
    }
}
