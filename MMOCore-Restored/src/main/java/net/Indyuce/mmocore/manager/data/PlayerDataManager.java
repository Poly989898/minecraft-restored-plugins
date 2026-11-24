package net.Indyuce.mmocore.manager.data;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.comp.profile.MMOCoreProfileDataModule;
import net.Indyuce.mmocore.manager.data.yaml.YAMLPlayerDataHandler;
import net.Indyuce.mmocore.player.DefaultPlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerDataManager extends SynchronizedDataManager<PlayerData, OfflinePlayerData> {
   private DefaultPlayerData defaultData = DefaultPlayerData.DEFAULT;

   public PlayerDataManager(MMOCore plugin) {
      super(plugin, new YAMLPlayerDataHandler(plugin));
   }

   public PlayerData newPlayerData(MMOPlayerData playerData) {
      return new PlayerData(playerData);
   }

   public Object newProfileDataModule() {
      return new MMOCoreProfileDataModule();
   }

   public DefaultPlayerData getDefaultData() {
      return this.defaultData;
   }

   public void loadDefaultData(ConfigurationSection config) {
      this.defaultData = new DefaultPlayerData(config);
   }

   public void autosave() {
      super.autosave();
      MMOCore.plugin.nativeGuildManager.saveAll();
   }
}
