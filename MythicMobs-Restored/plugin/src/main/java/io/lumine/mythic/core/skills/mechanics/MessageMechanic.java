package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Set;
import java.util.UUID;

@MythicMechanic(
   author = "Ashijin",
   name = "message",
   aliases = {"msg", "m"},
   description = "Sends a message to the target entity"
)
public class MessageMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "audience",
      description = "The audience of the message"
   )
   private SkillAudience audience;
   protected PlaceholderString message;

   public MessageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;
      this.message = mlc.getPlaceholderString(new String[]{"message", "msg", "m"}, null);
      if (this.message == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'message' attribute is required.");
         this.message = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded message skill with message {0}", this.message.toString());
         this.audience = mlc.getAudience("audience", null);
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.audience != null) {
         String message = this.message.get(data, target);

         for (AbstractEntity at : SkillAudience.getEffective(this.audience, data, target)) {
            if (at.isPlayer()) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending message {0} to ", this.message, at.getName());
               at.asPlayer().sendMessage(message);
            }
         }

         return SkillResult.SUCCESS;
      } else if (target.isPlayer()) {
         if (data.hasExecutionAudience() && !data.getExecutionAudience().get().contains(target.getUniqueId())) {
            return SkillResult.SUCCESS;
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending message {0}", this.message);
            target.asPlayer().sendMessage(this.message.get(data, target));
            return SkillResult.SUCCESS;
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Failed to send message to non-player");
         return SkillResult.INVALID_TARGET;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      String message = this.message.get(data, target);
      if (this.audience != null) {
         for (AbstractEntity at : SkillAudience.getEffective(this.audience, data, data.getCaster().getEntity())) {
            if (at.isPlayer()) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending message {0} to ", this.message, at.getName());
               at.asPlayer().sendMessage(message);
            }
         }
      } else {
         Set<UUID> execAudience = data.hasExecutionAudience() ? data.getExecutionAudience().get() : null;

         for (AbstractEntity atx : data.getCaster().getLocation().getWorld().getPlayers()) {
            if (execAudience == null || execAudience.contains(atx.getUniqueId())) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending message {0} to ", this.message, atx.getName());
               atx.asPlayer().sendMessage(message);
            }
         }
      }

      return SkillResult.SUCCESS;
   }
}
