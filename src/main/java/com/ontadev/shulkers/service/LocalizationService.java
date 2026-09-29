package com.ontadev.shulkers.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ontadev.libs.ioc.annotation.stereotype.Service;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LocalizationService {

    private static final Gson GSON = new Gson();

    private final Map<String, String> translations = new HashMap<>();

    public LocalizationService(JavaPlugin plugin) {
        load(plugin);
    }

    private void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "ru_ru.json");

        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                log.warn("Failed to create data folder {}", file.getParentFile());
            }
            plugin.saveResource(file.getName(), false);
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            json.entrySet().forEach(entry -> translations.put(entry.getKey(), entry.getValue().getAsString()));
            log.info("Loaded {} localization entries from {}", translations.size(), file.getName());
        } catch (IOException e) {
            log.error("Failed to load localization file {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * @return перевод для ключа, либо сам ключ, если перевод не найден.
     */
    @NonNull
    public String getTranslation(String key) {
        return translations.getOrDefault(key, key);
    }
}
