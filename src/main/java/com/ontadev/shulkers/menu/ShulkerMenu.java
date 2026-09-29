package com.ontadev.shulkers.menu;

import com.ontadev.libs.menu.AbstractMenu;
import com.ontadev.libs.message.Message;
import com.ontadev.libs.player.PlayerSnapshot;
import com.ontadev.shulkers.service.ShulkerViewService;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerMenu extends AbstractMenu {

    private final ItemStack item;
    private final BlockStateMeta meta;
    private final ShulkerBox shulkerBox;
    private final Message title;

    public ShulkerMenu(ItemStack item, BlockStateMeta meta, ShulkerBox shulkerBox, Message title) {
        this.item = item;
        this.meta = meta;
        this.shulkerBox = shulkerBox;
        this.title = title;
        this.editable = true;
    }

    @Override
    public Message title(PlayerSnapshot snapshot) {
        return title;
    }

    @Override
    public int size() {
        return shulkerBox.getInventory().getSize();
    }

    @Override
    public void onOpen(PlayerSnapshot snapshot, Player player, Inventory inventory) {
        //noinspection NullableProblems
        inventory.setContents(shulkerBox.getInventory().getContents());
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0f, 1.0f);
    }

    @Override
    public void onClose(PlayerSnapshot snapshot, Player player, Inventory inventory) {
        //noinspection NullableProblems
        shulkerBox.getInventory().setContents(inventory.getContents());
        meta.setBlockState(shulkerBox);
        item.setItemMeta(meta);

        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1f, 1f);
    }

    @Override
    public boolean onClick(PlayerSnapshot snapshot, InventoryClickEvent event) {
        if (ShulkerViewService.isShulker(event.getCursor()) || ShulkerViewService.isShulker(event.getCurrentItem())) {
            event.setCancelled(true);
            return true;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            ItemStack incoming = hotbarSlot >= 0
                    ? event.getWhoClicked().getInventory().getItem(hotbarSlot)
                    : null;

            if (ShulkerViewService.isShulker(incoming)) {
                event.setCancelled(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onDrag(PlayerSnapshot snapshot, InventoryDragEvent event) {
        if (ShulkerViewService.isShulker(event.getOldCursor())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrop(PlayerSnapshot snapshot, PlayerDropItemEvent event) {
        if (ShulkerViewService.isShulker(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
