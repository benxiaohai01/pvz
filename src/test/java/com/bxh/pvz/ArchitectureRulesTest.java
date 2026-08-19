package com.bxh.pvz;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 轻量级包边界保护：读取源码中的导入语句，防止后续重构重新引入依赖环。
 */
class ArchitectureRulesTest {

    private static final Path SOURCE_ROOT = Path.of("src/main/java/com/bxh/pvz");

    @Test
    void lowerLayersDoNotDependOnPresentationOrCompositionRoot() throws IOException {
        assumeTrue(Files.isDirectory(SOURCE_ROOT));

        assertNoImports("event", Set.of("com.bxh.pvz.core", "com.bxh.pvz.state", "com.bxh.pvz.service", "com.bxh.pvz.controller", "com.bxh.pvz.view"));
        assertNoImports("service", Set.of("com.bxh.pvz.core", "com.bxh.pvz.state", "com.bxh.pvz.controller", "com.bxh.pvz.view"));
        assertNoImports("view", Set.of("com.bxh.pvz.core", "com.bxh.pvz.state", "com.bxh.pvz.service"));
        assertNoImports("controller", Set.of("com.bxh.pvz.core", "com.bxh.pvz.view", "com.bxh.pvz.renderer"));
        assertNoImports("config", Set.of("com.bxh.pvz.model", "com.bxh.pvz.core", "com.bxh.pvz.service", "com.bxh.pvz.controller", "com.bxh.pvz.view"));
        assertNoImports("model", Set.of("com.bxh.pvz.core", "com.bxh.pvz.state", "com.bxh.pvz.service", "com.bxh.pvz.controller", "com.bxh.pvz.view", "com.bxh.pvz.renderer"));
    }

    private static void assertNoImports(String subpackage, Set<String> forbiddenPrefixes) throws IOException {
        Path root = SOURCE_ROOT.resolve(subpackage);
        try (var files = Files.walk(root)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                for (String line : Files.readAllLines(file)) {
                    if (line.startsWith("import ")) {
                        for (String prefix : forbiddenPrefixes) {
                            if (line.startsWith("import " + prefix + ".")) {
                                fail(file + " imports forbidden package: " + line);
                            }
                        }
                    }
                }
            }
        }
    }
}
