package net.Indyuce.mmocore.experience.dispenser;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.EXPSource;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface ExperienceDispenser {
   void giveExperience(PlayerData playerData, double value, @Nullable Location location, @NotNull EXPSource expSource);

   boolean shouldHandle(PlayerData playerData);

   @Nullable
   default Location getPlayerLocation(PlayerData player) {
      return player.isOnline() ? MMOCoreUtils.getCenterLocation(player.getPlayer()) : null;
   }
}
