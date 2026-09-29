package com.ontadev.shulkers;

import com.ontadev.libs.ioc.PluginIoC;
import com.ontadev.libs.plugin.OntaDev_Template;
import com.ontadev.shulkers.service.DefaultGroupPermissionService;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.Getter;
import org.bukkit.entity.Player;

public final class OntaDevShulkersPlugin extends OntaDev_Template {

    @Getter
    private static final StateFlag OPEN_SHULKERS = new StateFlag("open-shulkers", true);

    @Override
    public void onLoad() {
        WorldGuard.getInstance().getFlagRegistry().register(OPEN_SHULKERS);
        super.onLoad();
    }

    @Override
    public void onPluginEnable(PluginIoC pluginIoC) {
        pluginIoC.get(DefaultGroupPermissionService.class).apply();
        log.info("OntaDev_Shulkers enabled");
    }

    public boolean canOpenShulkers(Player player) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        return query.testState(BukkitAdapter.adapt(player.getLocation()), localPlayer, OPEN_SHULKERS);
    }
}
