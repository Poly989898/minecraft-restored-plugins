package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Joshinn",
   name = "log",
   aliases = {"console", "print, printf"},
   description = "Logs to console directly for debugging or preservation."
)
public class LogMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill, ITargetedLocationSkill {
   @MythicField(
      name = "message",
      aliases = {"m"},
      description = "The message to print"
   )
   private final PlaceholderString message;

   public LogMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.message = mlc.getPlaceholderString(new String[]{"message", "m"}, "");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String print = this.message.get(data, target);
      MythicLogger.log(print);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      String print = this.message.get(data);
      MythicLogger.log(print);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      String print = this.message.get(data, target);
      MythicLogger.log(print);
      return SkillResult.SUCCESS;
   }
}
