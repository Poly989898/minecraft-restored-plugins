package net.Indyuce.mmocore.experience;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExperienceObject extends ExperienceDispenser {
   String getKey();

   @Nullable
   ExpCurve getExpCurve();

   @NotNull
   ExperienceTable getExperienceTable();

   boolean hasExperienceTable();

   default void resetAdvancement(@NotNull PlayerData playerData, boolean levels) {
      if (this.hasExperienceTable()) {
         this.getExperienceTable().unclaim(playerData, this, levels);
      }
   }

   default void updateAdvancement(@NotNull PlayerData playerData, int newLevel) {
      if (this.hasExperienceTable()) {
         this.getExperienceTable().claim(playerData, newLevel, this);
      }
   }
}
