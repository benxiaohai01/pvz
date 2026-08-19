package com.bxh.pvz.renderer;

import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * 图片资源目录：集中加载背景、植物卡片与植物动画帧，供视图和渲染器读取。
 * 资源路径约定为：
 * 背景：assets/background/{backgroundImage}
 * 卡牌：assets/cards/{cardImage}
 * 植物动画：assets/plants/{spriteKey}/1.png 到 frameCount.png
 */
public final class SpriteCatalog {

    private static final String ASSET_ROOT = "/assets/";

    /** 植物配置目录，用于读取素材键、帧数与卡牌文件名。 */
    private final PlantCatalog plantCatalog;
    /** 白天背景图片；加载失败时允许为 null，由渲染器使用占位背景。 */
    private final Image daytimeBackground;
    /** 植物类型对应的动画帧数组，帧号从 1 开始连续编号。 */
    private final Map<PlantType, Image[]> plantFrames;
    /** 植物类型对应的顶部卡牌图片。 */
    private final Map<PlantType, Image> cardImages;

    public SpriteCatalog(PlantCatalog plantCatalog) {
        this.plantCatalog = plantCatalog;
        this.daytimeBackground = load("background/daytimeBg.jpg");
        this.plantFrames = loadPlantFrames();
        this.cardImages = loadCardImages();
    }

    public Image daytimeBackground() {
        return daytimeBackground;
    }

    public Image cardOf(PlantType type) {
        return cardImages.get(type);
    }

    /** 根据累计游戏时间和配置帧率选择植物当前动画帧。 */
    public Image frameOf(PlantType type, double elapsed) {
        Image[] frames = plantFrames.get(type);
        if (frames == null || frames.length == 0) {
            return null;
        }
        double animationFps = plantCatalog.of(type).animationFps();
        int frameIndex = (int) Math.floor(elapsed * animationFps) % frames.length;
        return frames[frameIndex];
    }

    private Map<PlantType, Image[]> loadPlantFrames() {
        Map<PlantType, Image[]> frames = new EnumMap<>(PlantType.class);
        for (PlantType type : PlantType.values()) {
            PlantConfig config = plantCatalog.of(type);
            if (config.spriteKey() == null) {
                continue;
            }
            int count = config.frameCount();
            Image[] images = new Image[count];
            // 配置约定帧文件从 1.png 开始，因此数组下标需要加一。
            for (int i = 0; i < count; i++) {
                images[i] = requireImage("plants/" + config.spriteKey() + "/" + (i + 1) + ".png");
            }
            frames.put(type, images);
        }
        return frames;
    }

    private Map<PlantType, Image> loadCardImages() {
        Map<PlantType, Image> cards = new EnumMap<>(PlantType.class);
        for (PlantType type : PlantType.values()) {
            PlantConfig config = plantCatalog.of(type);
            if (config.cardImage() == null) {
                continue;
            }
            cards.put(type, requireImage("cards/" + config.cardImage()));
        }
        return cards;
    }

    private Image load(String path) {
        URL resourceUrl = SpriteCatalog.class.getResource(ASSET_ROOT + path);
        if (resourceUrl == null) {
            return null;
        }
        // 同步加载图片并立即检查解码错误，便于视图尽早退回占位图形。
        Image image = new Image(resourceUrl.toExternalForm(), false);
        return image.isError() ? null : image;
    }

    private Image requireImage(String path) {
        Image image = load(path);
        if (image == null) {
            throw new IllegalStateException("缺少图片资源: " + ASSET_ROOT + path);
        }
        return image;
    }
}
