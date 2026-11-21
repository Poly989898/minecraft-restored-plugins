package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractGameMode;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Seyarada",
   name = "gamemode",
   aliases = {"gm"},
   description = "Matches the target's gamemode"
)
public class GamemodeCondition extends SkillCondition implements IEntityCondition {
   protected AbstractGameMode mode = AbstractGameMode.SURVIVAL;

   public GamemodeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strMode = mlc.getString(new String[]{"mode", "m"}, "SURVIVAL");

      try {
         this.mode = AbstractGameMode.valueOf(strMode.toUpperCase());
      } catch (Exception e) {
         MythicLogger.errorCondition(this, "'" + strMode + "' is not a valid GameMode");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      return target.isPlayer() ? target.asPlayer().getGameMode().equals(this.mode) : false;
   }
}
