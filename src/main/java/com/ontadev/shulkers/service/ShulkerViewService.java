package com.ontadev.shulkers.service;

import com.ontadev.libs.ioc.annotation.AutoListener;
import com.ontadev.libs.ioc.annotation.stereotype.Service;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AutoListener
public class ShulkerViewService implements Listener {

    private static final Set<Material> SHULKER_MATERIALS = EnumSet.of(
            Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX
    );

    private final Map<UUID, ViewSession> activeSessions = new ConcurrentHashMap<>();

    private final LocalizationService localizationService;

    public ShulkerViewService(LocalizationService localizationService) {
        this.localizationService = localizationService;
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
        Inventory realInventory = shulkerBox.getInventory();

        Component title = resolveTitle(shulkerItem, itemMeta);

        Inventory view = Bukkit.createInventory(null, realInventory.getSize(), title);
        //noinspection NullableProblems
        view.setContents(realInventory.getContents());

        activeSessions.put(player.getUniqueId(), new ViewSession(shulkerItem, meta, shulkerBox, view));
        player.openInventory(view);
        return true;
    }

    private Component resolveTitle(ItemStack shulkerItem, ItemMeta itemMeta) {
        Component displayName = itemMeta.displayName();
        if (displayName instanceof TextComponent) {
            return displayName;
        }
        return Component.text(localizationService.getTranslation(shulkerItem.getTranslationKey()));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ViewSession session = activeSessions.get(event.getWhoClicked().getUniqueId());
        if (session == null || event.getView().getTopInventory() != session.view) return;

        if (isShulker(event.getCursor()) || isShulker(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        ViewSession session = activeSessions.get(event.getWhoClicked().getUniqueId());
        if (session == null || event.getView().getTopInventory() != session.view) return;

        if (isShulker(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!activeSessions.containsKey(event.getPlayer().getUniqueId())) return;

        if (isShulker(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        ViewSession session = activeSessions.remove(event.getPlayer().getUniqueId());
        if (session == null || event.getView().getTopInventory() != session.view) return;

        persist(session);

        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1f, 1f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ViewSession session = activeSessions.remove(event.getPlayer().getUniqueId());
        if (session == null) return;

        persist(session);
    }

    private void persist(ViewSession session) {
        //noinspection NullableProblems
        session.shulkerBox.getInventory().setContents(session.view.getContents());
        session.meta.setBlockState(session.shulkerBox);
        session.item.setItemMeta(session.meta);
    }

    private static final class ViewSession {
        private final ItemStack item;
        private final BlockStateMeta meta;
        private final ShulkerBox shulkerBox;
        private final Inventory view;

        private ViewSession(ItemStack item, BlockStateMeta meta, ShulkerBox shulkerBox, Inventory view) {
            this.item = item;
            this.meta = meta;
            this.shulkerBox = shulkerBox;
            this.view = view;
        }
    }
}
