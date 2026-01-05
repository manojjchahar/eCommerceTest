package com.ecommercefull.utils;

import com.moandjiezana.toml.Toml;

import java.io.InputStream;
import java.util.Optional;

public final class ConfigReader {

    private static final Toml CONFIG;

    static {
        CONFIG = loadToml("/config/config.toml");
    }

    private ConfigReader() { }

    private static Toml loadToml(String path) {
        try (InputStream is = ConfigReader.class.getResourceAsStream(path)) {
            if (is == null) {
                // Fallback to legacy location root of resources
                try (InputStream legacy = ConfigReader.class.getResourceAsStream("/config.toml")) {
                    if (legacy != null) {
                        return new Toml().read(legacy);
                    }
                }
                return new Toml();
            }
            return new Toml().read(is);
        } catch (Exception e) {
            return new Toml();
        }
    }

    public static String getUiConfig(String key, String defaultValue) {
        return Optional.ofNullable(CONFIG.getTable("ui"))
                .map(t -> t.getString(key))
                .orElse(defaultValue);
    }

    public static String getApiConfig(String key, String defaultValue) {
        return Optional.ofNullable(CONFIG.getTable("api"))
                .map(t -> t.getString(key))
                .orElse(defaultValue);
    }
}
