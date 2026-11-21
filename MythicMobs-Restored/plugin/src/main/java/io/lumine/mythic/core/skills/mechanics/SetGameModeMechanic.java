package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractGameMode;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setgamemode",
   description = "Sets the gamemode of the target player"
)
public class SetGameModeMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "The game mode to apply to the target player",
      defValue = "SURVIVAL"
   )
   private PlaceholderString mode;

   public SetGameModeMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
      this.mode = mlc.getPlaceholderString(new String[]{"mode", "m"}, "SURVIVAL");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         String strInput = this.mode.get(data, target);
         AbstractGameMode mode = AbstractGameMode.fromString(strInput);
         if (mode == null) {
            MythicLogger.errorMechanicConfig(this, this.getConfig(), "'" + strInput + "' is not a valid GameMode");
            return SkillResult.INVALID_CONFIG;
         } else {
            target.asPlayer().setGameMode(mode);
            return SkillResult.SUCCESS;
         }
      }
   }
}
