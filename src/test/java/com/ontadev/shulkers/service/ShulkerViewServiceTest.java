package com.ontadev.shulkers.service;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShulkerViewServiceTest {

    @Test
    void nullItemIsNotAShulker() {
        assertFalse(ShulkerViewService.isShulker(null));
    }

    @ParameterizedTest
    @EnumSource(value = Material.class, names = "(?!LEGACY_)(.*_SHULKER_BOX|SHULKER_BOX)", mode = EnumSource.Mode.MATCH_ANY)
    void everyShulkerBoxColorIsRecognised(Material material) {
        assertTrue(ShulkerViewService.isShulker(new ItemStack(material)));
    }

    @ParameterizedTest
    @EnumSource(value = Material.class, names = {"CHEST", "STONE", "DIAMOND", "BARREL", "ENDER_CHEST"})
    void nonShulkerMaterialsAreRejected(Material material) {
        assertFalse(ShulkerViewService.isShulker(new ItemStack(material)));
    }
}
