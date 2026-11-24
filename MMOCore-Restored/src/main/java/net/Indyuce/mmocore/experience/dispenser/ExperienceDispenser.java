package net.Indyuce.mmocore.experience.dispenser;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.EXPSource;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface ExperienceDispenser {
   void giveExperience(PlayerData var1, double var2, @Nullable Location var4, @NotNull EXPSource var5);

   boolean shouldHandle(PlayerData var1);

   @Nullable
   default Location getPlayerLocation(PlayerData player) {
      return player.isOnline() ? MMOCoreUtils.getCenterLocation(player.getPlayer()) : null;
   }
}
