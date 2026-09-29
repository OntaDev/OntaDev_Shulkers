package com.ontadev.shulkers.listener;

import com.ontadev.libs.ioc.annotation.AutoListener;
import com.ontadev.libs.ioc.annotation.stereotype.Component;
import com.ontadev.shulkers.OntaDevShulkersPlugin;
import com.ontadev.shulkers.service.ShulkerViewService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Component
@AutoListener
public class ShulkerInteractListener implements Listener {

    private final OntaDevShulkersPlugin plugin;
    private final ShulkerViewService shulkerViewService;

    public ShulkerInteractListener(OntaDevShulkersPlugin plugin, ShulkerViewService shulkerViewService) {
        this.plugin = plugin;
        this.shulkerViewService = shulkerViewService;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();

        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR || !ShulkerViewService.isShulker(item)) return;

        if (!player.hasPermission("shulkers.open")) return;
        if (!plugin.canOpenShulkers(player)) return;

        if (shulkerViewService.open(player, item)) {
            event.setCancelled(true);
        }
    }
}
