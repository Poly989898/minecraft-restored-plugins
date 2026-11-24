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

   public void saveInSection(PlayerData var1, ConfigurationSection var2) {
      var2.createSection("crafting-queue");
      var2.set("permissions-from-items", new ArrayList<>(var1.getPermissions()));
      var1.getCrafting().save(var2.getConfigurationSection("crafting-queue"));
   }

   public void loadFromSection(PlayerData var1, ConfigurationSection var2) {
      if (var2.contains("crafting-queue")) {
         var1.getCrafting().load(var1, var2.getConfigurationSection("crafting-queue"));
      }

      if (MMOItems.plugin.hasPermissions() && var2.contains("permissions-from-items")) {
         Permission var3 = MMOItems.plugin.getVault().getPermissions();
         var2.getStringList("permissions-from-items").forEach(var2x -> {
            if (var3.has(var1.getPlayer(), var2x)) {
               var3.playerRemove(var1.getPlayer(), var2x);
            }
         });
      }
   }

   public void setup() {
   }

   public DefaultOfflineDataHolder getOffline(@NotNull UUID var1) {
      return new DefaultOfflineDataHolder(var1);
   }

   public void close() {
   }
}
