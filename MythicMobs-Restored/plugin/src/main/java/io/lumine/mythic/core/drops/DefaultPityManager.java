package io.lumine.mythic.core.drops;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerData;
import java.util.Optional;
import java.util.UUID;

public class DefaultPityManager extends PityManager {
   @Override
   public void addPity(UUID player, double amount) {
      this.addPity(player, amount, "DEFAULT");
   }

   @Override
   public void addPity(UUID player, double amount, String category) {
      Optional<PlayerData> playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
      if (!playerData.isEmpty()) {
         playerData.get().addPity(amount, category);
      }
   }

   @Override
   public void resetPity(UUID player) {
      this.resetPity(player, "DEFAULT");
   }

   @Override
   public void resetPity(UUID player, String category) {
      Optional<PlayerData> playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
      if (!playerData.isEmpty()) {
         playerData.get().resetPity(category);
      }
   }

   @Override
   public void setPity(UUID player, double amount) {
      this.setPity(player, amount, "DEFAULT");
   }

   @Override
   public void setPity(UUID player, double amount, String category) {
      Optional<PlayerData> playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
      if (!playerData.isEmpty()) {
         playerData.get().setPity(amount, category);
      }
   }

   @Override
   public void removePity(UUID player, double amount) {
      this.removePity(player, amount, "DEFAULT");
   }

   @Override
   public void removePity(UUID player, double amount, String category) {
      Optional<PlayerData> playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
      if (!playerData.isEmpty()) {
         playerData.get().removePity(amount, category);
      }
   }

   @Override
   public double getPity(UUID player) {
      return this.getPity(player, "DEFAULT");
   }

   @Override
   public double getPity(UUID player, String category) {
      Optional<PlayerData> playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
      return playerData.<Double>map(data -> data.getPity(category)).orElse(0.0);
   }
}
