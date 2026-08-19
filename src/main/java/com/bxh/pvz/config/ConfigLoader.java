package com.bxh.pvz.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 从类路径加载 JSON 内容配置（数据驱动：数值与代码分离）。
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
        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalStateException("未找到配置文件: " + path);
            }
            return MAPPER.readValue(inputStream, type);
        } catch (IOException e) {
            throw new UncheckedIOException("配置文件读取失败: " + path, e);
        }
    }

    private static <K, V> Map<K, V> indexBy(List<V> values, Function<V, K> keyExtractor) {
        return values.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()));
    }
}
