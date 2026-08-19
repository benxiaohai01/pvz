package com.bxh.pvz;

import com.bxh.pvz.TestFixture;
import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.PlantCatalog;
import com.bxh.pvz.config.ZombieCatalog;
import com.bxh.pvz.factory.PlantFactory;
import com.bxh.pvz.factory.ZombieFactory;
import com.bxh.pvz.model.world.GameWorld;

/**
 * 单元测试共享的生产配置目录。
 */
public final class TestFixture {

    private static final PlantCatalog PLANT_CATALOG = new PlantCatalog();
    private static final ZombieCatalog ZOMBIE_CATALOG = new ZombieCatalog();
    private static final LevelCatalog LEVEL_CATALOG = new LevelCatalog(PLANT_CATALOG, ZOMBIE_CATALOG);
    private static final PlantFactory PLANT_FACTORY = new PlantFactory(PLANT_CATALOG);
    private static final ZombieFactory ZOMBIE_FACTORY = new ZombieFactory(ZOMBIE_CATALOG);

    private TestFixture() {
    }

    public static PlantCatalog plants() {
        return PLANT_CATALOG;
    }

    public static ZombieCatalog zombies() {
        return ZOMBIE_CATALOG;
    }

    public static LevelCatalog levels() {
        return LEVEL_CATALOG;
    }

    public static LevelConfig level(String id) {
        return LEVEL_CATALOG.byId(id);
    }

    public static PlantFactory plantFactory() {
        return PLANT_FACTORY;
    }

    public static ZombieFactory zombieFactory() {
        return ZOMBIE_FACTORY;
    }

    public static GameWorld world(String levelId) {
        return new GameWorld(level(levelId));
    }
}
