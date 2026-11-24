package com.nametagedit.plugin;

public interface INametagApi {
    void setNametag(String player, String prefix, String suffix);
    void setNametag(org.bukkit.entity.Player player, String prefix, String suffix);
    void setPrefix(String player, String prefix);
    void setPrefix(org.bukkit.entity.Player player, String prefix);
    void setSuffix(String player, String suffix);
    void setSuffix(org.bukkit.entity.Player player, String suffix);
}
