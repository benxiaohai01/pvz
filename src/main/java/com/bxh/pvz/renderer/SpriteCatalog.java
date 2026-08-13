package com.bxh.pvz.renderer;

import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.PlantConfig;
import com.bxh.pvz.config.PlantType;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

/**
 * 图片资源目录：集中加载背景、植物卡片与植物动画帧。
 */
public final class SpriteCatalog {

    private static final String ASSET_ROOT = "/assets/";

    private final PlantCatalog plantCatalog;
    private final Image daytimeBackground;
    private final Map<PlantType, Image[]> plantFrames;
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

    /** 根据配置的帧数与帧率选择植物当前动画帧。 */
    public Image frameOf(PlantType type, double elapsed) {
        Image[] frames = plantFrames.get(type);
        if (frames == null || frames.length == 0) {
            return null;
        }
        double fps = plantCatalog.of(type).animationFps();
        int index = (int) Math.floor(elapsed * fps) % frames.length;
        return frames[index];
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
        var url = SpriteCatalog.class.getResource(ASSET_ROOT + path);
        if (url == null) {
            return null;
        }
        Image image = new Image(url.toExternalForm(), false);
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
