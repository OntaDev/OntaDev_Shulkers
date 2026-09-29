package com.ontadev.shulkers.config;

import com.ontadev.libs.config.YamlConfig;
import com.ontadev.libs.ioc.annotation.stereotype.Config;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Config
public class ShulkersConfig extends YamlConfig {

    private boolean defaultShulkerPermission = true;

    @Override
    public String getFileName() {
        return "config";
    }
}
