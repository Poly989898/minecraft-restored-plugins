package net.Indyuce.mmocore.manager.data.yaml;

import java.util.List;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class YAMLOfflinePlayerData implements OfflinePlayerData {
   private final UUID uuid;
   private final ConfigFile config;

   @Deprecated
   public YAMLOfflinePlayerData(UUID uuid) {
      this.uuid = uuid;
      this.config = new ConfigFile(uuid);
   }

   @NotNull
   public UUID getUniqueId() {
      return this.uuid;
   }

   @Override
   public void removeFriend(UUID uuid) {
      List<String> friends = this.config.getConfig().getStringList("friends");
      friends.remove(uuid.toString());
      this.config.getConfig().set("friends", friends);
      this.config.save();
   }

   @Override
   public boolean hasFriend(UUID uuid) {
      return this.config.getConfig().getStringList("friends").contains(uuid.toString());
   }

   @Override
   public PlayerClass getProfess() {
      return this.config.getConfig().contains("class")
         ? MMOCore.plugin.classManager.get(this.config.getConfig().getString("class"))
         : MMOCore.plugin.classManager.getDefaultClass();
   }

   @Override
   public int getLevel() {
      return this.config.getConfig().getInt("level");
   }

   @Override
   public long getLastLogin() {
      return this.config.getConfig().getLong("last-login");
   }
}
