package net.Indyuce.mmoitems.manager.data;

import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.yaml.YAMLSynchronizedDataHandler;
import java.util.ArrayList;
import java.util.UUID;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class YAMLDataHandler extends YAMLSynchronizedDataHandler<PlayerData, DefaultOfflineDataHolder> {
   public YAMLDataHandler() {
      super(MMOItems.plugin);
   }

   public void saveInSection(PlayerData playerData, ConfigurationSection section) {
      section.createSection("crafting-queue");
      section.set("permissions-from-items", new ArrayList<>(playerData.getPermissions()));
      playerData.getCrafting().save(section.getConfigurationSection("crafting-queue"));
   }

   public void loadFromSection(PlayerData playerData, ConfigurationSection section) {
      if (section.contains("crafting-queue")) {
         playerData.getCrafting().load(playerData, section.getConfigurationSection("crafting-queue"));
      }

      if (MMOItems.plugin.hasPermissions() && section.contains("permissions-from-items")) {
         Permission permissions = MMOItems.plugin.getVault().getPermissions();
         section.getStringList("permissions-from-items").forEach(var2x -> {
            if (permissions.has(playerData.getPlayer(), var2x)) {
               permissions.playerRemove(playerData.getPlayer(), var2x);
            }
         });
      }
   }

   public void setup() {
   }

   public DefaultOfflineDataHolder getOffline(@NotNull UUID uuid) {
      return new DefaultOfflineDataHolder(uuid);
   }

   public void close() {
   }
}
