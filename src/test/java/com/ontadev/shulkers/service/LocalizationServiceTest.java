package com.ontadev.shulkers.service;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LocalizationServiceTest {

    @Test
    void readsExistingFileAndFallsBackToKeyWhenTranslationMissing(@TempDir Path tempDir) throws IOException {
        Files.write(tempDir.resolve("ru_ru.json"),
                "{\"block.minecraft.shulker_box\":\"Шалкер\"}".getBytes(StandardCharsets.UTF_8));

        JavaPlugin plugin = mock(JavaPlugin.class);
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());

        LocalizationService service = new LocalizationService(plugin);

        assertEquals("Шалкер", service.getTranslation("block.minecraft.shulker_box"));
        assertEquals("unknown.key", service.getTranslation("unknown.key"));
        verify(plugin, never()).saveResource(any(), anyBoolean());
    }

    @Test
    void copiesBundledResourceWhenFileIsMissing(@TempDir Path tempDir) {
        JavaPlugin plugin = mock(JavaPlugin.class);
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        doAnswer(invocation -> {
            Files.write(tempDir.resolve("ru_ru.json"), "{\"a\":\"b\"}".getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(plugin).saveResource(eq("ru_ru.json"), eq(false));

        LocalizationService service = new LocalizationService(plugin);

        assertEquals("b", service.getTranslation("a"));
        verify(plugin).saveResource("ru_ru.json", false);
    }
}
