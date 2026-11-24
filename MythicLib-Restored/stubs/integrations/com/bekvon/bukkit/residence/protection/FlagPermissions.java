package com.bekvon.bukkit.residence.protection;
import com.bekvon.bukkit.residence.containers.Flags;
public class FlagPermissions {
    public static void addFlag(String flag) {}
    public boolean has(Flags flag, boolean def) { return def; }
    public boolean has(String flag, boolean def, boolean other) { return def; }
    public boolean playerHas(org.bukkit.entity.Player player, String flag, boolean def) { return def; }
}
