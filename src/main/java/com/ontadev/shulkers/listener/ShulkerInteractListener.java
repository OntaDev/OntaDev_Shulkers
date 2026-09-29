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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Открывает шалкер по клику в воздух с ним в руке (в основной или второй руке) -
 * то же поведение, что было в исходном ClickListener, но без бага: там предмет из
 * события сверялся со слотами хотбара (0-8) через поиск похожего стека, и для
 * шалкера во второй руке (offhand, слот 40) такой поиск всегда проваливался -
 * функция открытия молча не работала. {@link PlayerInteractEvent#getItem()} уже
 * возвращает предмет из той руки, что вызвала событие, поэтому слот искать не нужно.
 * <p>
 * Помечен и {@code @Component}, и {@code @AutoListener}: сам по себе {@code @AutoListener}
 * в OntaDev_Libs не регистрирует класс как компонент для создания (см.
 * {@code AutoListenerHandler#handle}, который только валидирует класс) - фактическая
 * регистрация в Bukkit происходит в {@code postCreate}, а он вызывается лишь для классов,
 * которые IoC-контейнер реально создал. Без {@code @Component} этот листенер IoC просто
 * не увидел бы, и обработчик событий никогда бы не сработал.
 */
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

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR || !ShulkerViewService.isShulker(item)) return;

        Player player = event.getPlayer();

        if (!player.hasPermission("shulkers.open")) return;
        if (!plugin.canOpenShulkers(player)) return;

        if (shulkerViewService.open(player, item)) {
            event.setCancelled(true);
        }
    }
}
