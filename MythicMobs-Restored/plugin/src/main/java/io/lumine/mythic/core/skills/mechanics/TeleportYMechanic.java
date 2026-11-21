package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "teleporty",
   aliases = {"tpy"},
   description = "Teleports to the target Y coordinate"
)
public class TeleportYMechanic extends SkillMechanic implements INoTargetSkill {
   protected PlaceholderDouble y;
   protected AbstractTeleportData teleportData;

   public TeleportYMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.y = mlc.getPlaceholderDouble(new String[]{"y"}, 0.0);
      this.teleportData = this.getPlugin().getBootstrap().createTeleportData(mlc);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      AbstractLocation target = data.getCaster().getEntity().getLocation().clone();
      target.setY(this.y.get(data));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Teleporting to {0}", target.toString());
      data.getCaster().getEntity().teleport(target, this.teleportData);
      return SkillResult.SUCCESS;
   }
}
