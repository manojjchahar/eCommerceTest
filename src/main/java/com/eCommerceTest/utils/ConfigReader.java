package com.eCommerceTest.utils;

import com.moandjiezana.toml.Toml;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigReader {
    private static final Toml CONFIG;
    private static final Map<String,String> CACHE = new ConcurrentHashMap<>();

    static { CONFIG = loadToml("/config/config.toml"); }
    private ConfigReader() { }

    private static Toml loadToml(String path) {
        try (InputStream is = ConfigReader.class.getResourceAsStream(path)) {
            if (is == null) {
                // Fallback to legacy location root of resources
                try (InputStream legacy = ConfigReader.class.getResourceAsStream("/config.toml")) {
                    if (legacy != null) return new Toml().read(legacy);
                }
                return new Toml();
            }
            return new Toml().read(is);
        } catch (Exception e) { return new Toml(); }
    }

    public static String getUiConfig(String key, String defaultValue) {
        return resolve("ui." + key, defaultValue);
    }
    public static String getApiConfig(String key, String defaultValue) {
        return resolve("api." + key, defaultValue);
    }

    /** Generic dot-notation config fetch with system property precedence & cache */
    public static String get(String path, String defaultValue) { return resolve(path, defaultValue); }

    private static String resolve(String path, String defaultValue) {
        // System property precedence
        String sys = System.getProperty(path.replace('.', '_'));
        if (sys != null && !sys.isBlank()) return sys;
        return CACHE.computeIfAbsent(path, p -> lookup(p).orElse(defaultValue));
    }

    private static Optional<String> lookup(String path) {
        String[] parts = path.split("\\.");
        if (parts.length == 0) return Optional.empty();
        Toml current = CONFIG;
        for (int i = 0; i < parts.length - 1; i++) {
            Object table = current.getTable(parts[i]);
            if (!(table instanceof Toml)) return Optional.empty();
            current = (Toml) table;
        }
        return Optional.ofNullable(current.getString(parts[parts.length - 1]));
    }

    /** Allows explicit cache eviction (e.g., tests). */
    public static void evict(String path) { CACHE.remove(path); }
    public static void clearCache() { CACHE.clear(); }
}
