package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "sendactionmessage",
   aliases = {"actionmessage", "am"},
   description = "Send an Action Bar message to the target player"
)
public class SendActionMessageMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString message;

   public SendActionMessageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;

      try {
         this.message = PlaceholderString.of(mlc.getString(new String[]{"message", "msg", "m"}));
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'message' attribute is required.");
         this.message = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
         e.printStackTrace();
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         String message = this.message.get(data, target);
         target.asPlayer().sendActionBarMessage(message);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
