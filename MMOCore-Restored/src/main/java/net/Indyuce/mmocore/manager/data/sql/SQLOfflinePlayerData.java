package net.Indyuce.mmocore.manager.data.sql;

import java.util.List;
import java.util.UUID;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class SQLOfflinePlayerData implements OfflinePlayerData {
   private final UUID uuid;
   private int level;
   private long lastLogin;
   private PlayerClass profess;
   private List<UUID> friends;

   @Deprecated
   public SQLOfflinePlayerData(UUID uuid) {
      this.uuid = uuid;
   }

   @NotNull
   public UUID getUniqueId() {
      return this.uuid;
   }

   @Override
   public void removeFriend(UUID uuid) {
   }

   @Override
   public boolean hasFriend(UUID uuid) {
      return this.friends.contains(uuid);
   }

   @Override
   public PlayerClass getProfess() {
      return this.profess;
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   @Override
   public long getLastLogin() {
      return this.lastLogin;
   }
}
