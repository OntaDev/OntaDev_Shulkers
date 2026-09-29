package com.ontadev.shulkers.config;

import com.ontadev.libs.config.YamlConfig;
import com.ontadev.libs.ioc.annotation.stereotype.Config;
import lombok.Getter;
import lombok.Setter;

/**
 * Настройки плагина, управляемые OntaDev_Libs: файл config.yml создаётся/дополняется
 * автоматически из значений полей ниже, без ручной работы с Bukkit YamlConfiguration.
 */
@Getter
@Setter
@Config
public class ShulkersConfig extends YamlConfig {

    /**
     * Значение permission-ноды "shulkers.open", выставляемое группе LuckPerms "default"
     * при каждом старте плагина (см. {@link com.ontadev.shulkers.service.DefaultGroupPermissionService}).
     */
    private boolean defaultShulkerPermission = true;

    @Override
    public String getFileName() {
        return "config";
    }
}
