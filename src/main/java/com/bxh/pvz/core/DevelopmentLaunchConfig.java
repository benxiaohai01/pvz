package com.bxh.pvz.core;

import com.bxh.pvz.config.GameConfig;
import com.bxh.pvz.config.LevelCatalog;
import com.bxh.pvz.config.LevelConfig;
import com.bxh.pvz.config.PlantType;
import com.bxh.pvz.config.ZombieType;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 开发调试启动配置。
 *
 * <p>该配置只服务于本地调试：指定后可以让程序跳过主菜单、选关和选植物，
 * 直接进入目标关卡。未指定时完全走正式启动流程，不影响线上或正常开发流程。</p>
 */
public record DevelopmentLaunchConfig(
        boolean directToGame,
        String levelId,
        List<PlantType> plantTypes,
        List<ZombieType> zombieTypes) {

    /** Java 系统属性形式的调试场景键。 */
    public static final String SCENE_SYSTEM_PROPERTY = "pvz.dev.scene";
    /** Java 系统属性形式的调试关卡键。 */
    public static final String LEVEL_SYSTEM_PROPERTY = "pvz.dev.level";
    /** Java 系统属性形式的调试植物键。 */
    public static final String PLANTS_SYSTEM_PROPERTY = "pvz.dev.plants";
    /** Java 系统属性形式的调试僵尸键。 */
    public static final String ZOMBIES_SYSTEM_PROPERTY = "pvz.dev.zombies";

    /** JavaFX 命名参数中使用的短键，例如 {@code --dev-scene=GAME}。 */
    private static final String SCENE_NAMED_PARAMETER = "dev-scene";
    private static final String LEVEL_NAMED_PARAMETER = "dev-level";
    private static final String PLANTS_NAMED_PARAMETER = "dev-plants";
    private static final String ZOMBIES_NAMED_PARAMETER = "dev-zombies";

    /** 只有该值表示调试直达游戏页面。 */
    private static final String GAME_SCENE = "GAME";
    /** 未指定关卡时默认打开第一关。 */
    private static final String DEFAULT_LEVEL_ID = "1-1";

    public DevelopmentLaunchConfig {
        Objects.requireNonNull(levelId, "调试关卡编号不能为空");
        plantTypes = List.copyOf(plantTypes);
        zombieTypes = List.copyOf(zombieTypes);

        if (directToGame && levelId.isBlank()) {
            throw new IllegalArgumentException("调试直达时必须指定关卡编号");
        }
        if (directToGame && plantTypes.isEmpty()) {
            throw new IllegalArgumentException("调试直达时至少需要一种默认植物");
        }
        if (directToGame && zombieTypes.isEmpty()) {
            throw new IllegalArgumentException("调试直达时至少需要一种默认僵尸");
        }
    }

    /**
     * 从 JavaFX 启动参数、JVM 系统属性和关卡目录生成配置。
     * 命名参数优先于系统属性，两者都未设置时返回正常启动配置。
     */
    public static DevelopmentLaunchConfig from(
            Application.Parameters parameters,
            LevelCatalog levelCatalog) {
        Objects.requireNonNull(levelCatalog, "关卡目录不能为空");

        Map<String, String> namedParameters = parameters == null
                ? Map.of()
                : parameters.getNamed();
        return resolve(
                firstNonBlank(
                        namedParameters.get(SCENE_SYSTEM_PROPERTY),
                        namedParameters.get(SCENE_NAMED_PARAMETER),
                        System.getProperty(SCENE_SYSTEM_PROPERTY)),
                firstNonBlank(
                        namedParameters.get(LEVEL_SYSTEM_PROPERTY),
                        namedParameters.get(LEVEL_NAMED_PARAMETER),
                        System.getProperty(LEVEL_SYSTEM_PROPERTY)),
                firstNonBlank(
                        namedParameters.get(PLANTS_SYSTEM_PROPERTY),
                        namedParameters.get(PLANTS_NAMED_PARAMETER),
                        System.getProperty(PLANTS_SYSTEM_PROPERTY)),
                firstNonBlank(
                        namedParameters.get(ZOMBIES_SYSTEM_PROPERTY),
                        namedParameters.get(ZOMBIES_NAMED_PARAMETER),
                        System.getProperty(ZOMBIES_SYSTEM_PROPERTY)),
                levelCatalog);
    }

    /** 创建完全不开启调试直达的正式启动配置。 */
    public static DevelopmentLaunchConfig standard() {
        return new DevelopmentLaunchConfig(false, "", List.of(), List.of());
    }

    /**
     * 用已读取的原始参数值解析配置；保留包内可见便于单元测试。
     */
    static DevelopmentLaunchConfig resolve(
            String sceneValue,
            String levelValue,
            String plantsValue,
            String zombiesValue,
            LevelCatalog levelCatalog) {
        String normalizedSceneValue = sceneValue == null ? null : sceneValue.trim();
        if (!GAME_SCENE.equalsIgnoreCase(normalizedSceneValue)) {
            return standard();
        }

        String selectedLevelId = defaultIfBlank(levelValue, DEFAULT_LEVEL_ID);
        LevelConfig selectedLevel = levelCatalog.byId(selectedLevelId);
        List<PlantType> selectedPlants = isBlank(plantsValue)
                ? defaultPlants(selectedLevel)
                : parseEnumList(plantsValue, PlantType.class, PLANTS_SYSTEM_PROPERTY);
        List<ZombieType> selectedZombies = isBlank(zombiesValue)
                ? defaultZombies()
                : parseEnumList(zombiesValue, ZombieType.class, ZOMBIES_SYSTEM_PROPERTY);

        return new DevelopmentLaunchConfig(
                true,
                selectedLevelId,
                limitPlants(selectedPlants),
                selectedZombies);
    }

    private static List<PlantType> defaultPlants(LevelConfig levelConfig) {
        return limitPlants(levelConfig.availablePlants());
    }

    private static List<PlantType> limitPlants(List<PlantType> plants) {
        if (plants.size() <= GameConfig.MAX_SELECTED_PLANTS) {
            return List.copyOf(plants);
        }
        return List.copyOf(plants.subList(0, GameConfig.MAX_SELECTED_PLANTS));
    }

    private static List<ZombieType> defaultZombies() {
        return Arrays.stream(ZombieType.values()).toList();
    }

    private static <T extends Enum<T>> List<T> parseEnumList(
            String rawValue,
            Class<T> enumType,
            String parameterName) {
        List<T> parsedTypes = new ArrayList<>();
        for (String item : rawValue.split(",")) {
            String normalizedItem = item.trim().toUpperCase(Locale.ROOT);
            if (normalizedItem.isEmpty()) {
                continue;
            }
            try {
                parsedTypes.add(Enum.valueOf(enumType, normalizedItem));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "调试参数 " + parameterName + " 包含未知枚举值: " + item,
                        exception);
            }
        }
        return parsedTypes.stream().distinct().toList();
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
