package com.eCommerceTest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic cached data repository for test resources (JSON under /testData or
 * /schemas).
 * Supersedes JSONUtils & TestDataLoader (both removed). Provides unified load
 * methods.
 */
public final class DataRepository {
    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final ConcurrentHashMap<String, CacheEntry> CACHE = new ConcurrentHashMap<>();

    private record CacheEntry(Object value, Instant loadedAt, long lastModified) {
    }

    @FunctionalInterface
    private interface Reader<T> {
        T read(InputStream is) throws Exception;
    }

    private DataRepository() {
    }

    public static <T> T load(String resourcePath, Class<T> type) {
        return type.cast(loadGeneric(resourcePath, is -> MAPPER.readValue(is, type)));
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(String resourcePath, TypeReference<T> ref) {
        return (T) loadGeneric(resourcePath, is -> MAPPER.readValue(is, ref));
    }

    public static String loadAsString(String resourcePath) {
        return (String) loadGeneric(resourcePath, is -> new String(is.readAllBytes()));
    }

    /** Force reload of a cached resource. */
    public static void evict(String resourcePath) {
        CACHE.remove(normalize(resourcePath));
    }

    /** Clears all cached entries. */
    public static void clear() {
        CACHE.clear();
    }

    private static Object loadGeneric(String resourcePath, Reader<Object> reader) {
        String key = normalize(resourcePath);
        try {
            long fsLastModified = findLastModified(resourcePath);
            CacheEntry entry = CACHE.get(key);
            if (entry != null && entry.lastModified == fsLastModified) {
                return entry.value();
            }
            try (InputStream is = DataRepository.class.getResourceAsStream(resourcePath)) {
                if (is == null)
                    throw new IllegalArgumentException("Resource not found: " + resourcePath);
                Object value = reader.read(is);
                CACHE.put(key, new CacheEntry(value, Instant.now(), fsLastModified));
                return value;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, e);
        }
    }

    private static long findLastModified(String resourcePath) {
        try {
            Path p = Path.of("src", "test", "resources",
                    resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
            if (Files.exists(p)) {
                return Files.getLastModifiedTime(p).toMillis();
            }
        } catch (Exception ignored) {
        }
        return 0L;
    }

    private static String normalize(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    // Convenience wrappers for common testData usage
    public static List<Map<String, Object>> loadJsonArray(String fileName) {
        return load(Constants.RES_TESTDATA_ROOT + fileName, new TypeReference<List<Map<String, Object>>>() {
        });
    }
}
