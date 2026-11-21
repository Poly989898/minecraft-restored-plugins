package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;

@MythicMechanic(
   author = "Ashijin",
   name = "setchunkforceloaded",
   description = "Sets the force-loaded status of a location's chunk"
)
public class SetChunkForceLoadedMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "loaded",
      aliases = {"l"},
      description = "Whether the target chunk should be force-loaded",
      defValue = "true"
   )
   private boolean loaded;

   public SetChunkForceLoadedMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.loaded = mlc.getBoolean(new String[]{"loaded", "l"}, true);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location bukkitLocation = BukkitAdapter.adapt(target);
      bukkitLocation.getChunk().load();
      bukkitLocation.getChunk().setForceLoaded(this.loaded);
      return SkillResult.SUCCESS;
   }
}
