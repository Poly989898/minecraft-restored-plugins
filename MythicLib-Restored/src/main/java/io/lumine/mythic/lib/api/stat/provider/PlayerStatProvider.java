package io.lumine.mythic.lib.api.stat.provider;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerStatProvider extends StatProvider {
   @NotNull
   default Player getPlayer() {
      return this.getData().getPlayer();
   }

   @NotNull
   PlayerMetadata cache(@NotNull EquipmentSlot var1);

   @NotNull
   MMOPlayerData getData();

   @NotNull
   @Override
   default LivingEntity getEntity() {
      return this.getData().getPlayer();
   }
}
