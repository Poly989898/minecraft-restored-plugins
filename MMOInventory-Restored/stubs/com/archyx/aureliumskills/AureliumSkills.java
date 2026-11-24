package com.archyx.aureliumskills;
import com.archyx.aureliumskills.data.PlayerData;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
public class AureliumSkills extends JavaPlugin {
    public PlayerManager getPlayerManager() { return null; }
    public static class PlayerManager { public PlayerData getPlayerData(UUID id) { return null; } }
}
