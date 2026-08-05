package com.pvz.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvz.model.entity.plant.PlantType;
import com.pvz.model.entity.zombie.ZombieType;
import com.pvz.model.level.LevelConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 从 classpath 加载 JSON 内容配置（数据驱动：数值与代码分离）。
 */
public final class ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigLoader() {
    }

    public static Map<PlantType, PlantConfig> loadPlants() {
        return indexBy(readList("/config/plants.json", new TypeReference<>() {
        }), PlantConfig::type);
    }

    public static Map<ZombieType, ZombieConfig> loadZombies() {
        return indexBy(readList("/config/zombies.json", new TypeReference<>() {
        }), ZombieConfig::type);
    }

    public static List<LevelConfig> loadLevels() {
        return readList("/config/levels.json", new TypeReference<>() {
        });
    }

    private static <T> List<T> readList(String path, TypeReference<List<T>> type) {
        try (InputStream in = ConfigLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("未找到配置文件: " + path);
            }
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("配置文件读取失败: " + path, e);
        }
    }

    private static <K, V> Map<K, V> indexBy(List<V> values, Function<V, K> keyFn) {
        return values.stream().collect(Collectors.toUnmodifiableMap(keyFn, Function.identity()));
    }
}
