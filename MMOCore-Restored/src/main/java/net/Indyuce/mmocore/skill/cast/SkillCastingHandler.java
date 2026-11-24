package net.Indyuce.mmocore.skill.cast;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class SkillCastingHandler implements Listener {
   private final int timeoutDelay;
   private final boolean timesOut;

   public SkillCastingHandler(@NotNull ConfigurationSection config) {
      this.timesOut = config.contains("time-out");
      this.timeoutDelay = config.getInt("time-out");
      Validate.isTrue(!this.timesOut || this.timeoutDelay > 0, "Timeout must be strictly positive or disabled");
   }

   public boolean doesTimeOut() {
      return this.timesOut;
   }

   public int getTimeoutDelay() {
      return this.timeoutDelay;
   }

   @NotNull
   public abstract SkillCastingMode getCastingMode();

   @NotNull
   public abstract SkillCastingInstance newInstance(@NotNull PlayerData playerData);

   public void onSkillBound(@NotNull PlayerData player) {
   }
}
