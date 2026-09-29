package com.ontadev.shulkers.service;

import com.ontadev.libs.ioc.annotation.stereotype.Service;
import com.ontadev.shulkers.config.ShulkersConfig;
import lombok.extern.slf4j.Slf4j;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;

@Slf4j
@Service
public class DefaultGroupPermissionService {

    private static final String PERMISSION = "shulkers.open";
    private static final String GROUP_NAME = "default";

    private final ShulkersConfig config;

    public DefaultGroupPermissionService(ShulkersConfig config) {
        this.config = config;
    }

    public void apply() {
        LuckPerms luckPerms;
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            log.warn("LuckPerms is not ready, skipping default permission bootstrap", e);
            return;
        }

        Group group = luckPerms.getGroupManager().getGroup(GROUP_NAME);
        if (group == null) {
            log.warn("LuckPerms group '{}' not found, skipping default permission bootstrap", GROUP_NAME);
            return;
        }

        PermissionNode permission = PermissionNode.builder()
                .permission(PERMISSION)
                .value(config.isDefaultShulkerPermission())
                .build();

        group.data().add(permission);

        luckPerms.getGroupManager().saveGroup(group).exceptionally(ex -> {
            log.error("Failed to save LuckPerms group '{}'", GROUP_NAME, ex);
            return null;
        });
    }
}
