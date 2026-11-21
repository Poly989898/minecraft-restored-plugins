package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "sendResourcePack",
   aliases = {"resourcePack"},
   description = "Sends a resource pack to the target player"
)
public class SendResourcePackMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private PlaceholderString url;
   private PlaceholderString hash;

   public SendResourcePackMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;
      this.url = mlc.getPlaceholderString(new String[]{"url", "u", "link"}, null);
      this.hash = mlc.getPlaceholderString(new String[]{"hash", "h"}, "mythicmobs");
      if (this.url == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "'URL' attribute must be set to a valid resource pack URL");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         MythicBukkit.inst().getVolatileCodeHandler().sendResourcePack(target.asPlayer(), this.url.get(data, target), this.hash.get(data, target));
         return SkillResult.SUCCESS;
      }
   }
}
