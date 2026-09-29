package com.ontadev.shulkers.service;

import com.ontadev.libs.ioc.annotation.stereotype.Service;
import com.ontadev.libs.menu.manager.MenuManager;
import com.ontadev.libs.message.Message;
import com.ontadev.shulkers.menu.ShulkerMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.Set;

@Service
public class ShulkerViewService {

    private static final Set<Material> SHULKER_MATERIALS = EnumSet.of(
            Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX
    );

    private final LocalizationService localizationService;
    private final MenuManager menuManager;

    public ShulkerViewService(LocalizationService localizationService, MenuManager menuManager) {
        this.localizationService = localizationService;
        this.menuManager = menuManager;
    }

    public static boolean isShulker(ItemStack item) {
        return item != null && SHULKER_MATERIALS.contains(item.getType());
    }

    public boolean open(Player player, ItemStack shulkerItem) {
        ItemMeta itemMeta = shulkerItem.getItemMeta();
        if (!(itemMeta instanceof BlockStateMeta)) return false;

        BlockStateMeta meta = (BlockStateMeta) itemMeta;
        if (!(meta.getBlockState() instanceof ShulkerBox)) return false;

        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        Component title = resolveTitle(shulkerItem, itemMeta);

        menuManager.open(new ShulkerMenu(shulkerItem, meta, shulkerBox, new Message(title)), player);
        return true;
    }

    private Component resolveTitle(ItemStack shulkerItem, ItemMeta itemMeta) {
        Component displayName = itemMeta.displayName();
        if (displayName instanceof TextComponent) {
            return displayName;
        }
        return Component.text(localizationService.getTranslation(shulkerItem.getTranslationKey()));
    }
}
