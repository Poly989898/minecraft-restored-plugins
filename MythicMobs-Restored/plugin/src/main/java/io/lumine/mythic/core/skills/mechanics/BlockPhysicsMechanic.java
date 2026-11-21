package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;

@MythicMechanic(
   author = "Ashijin",
   name = "blockphysics",
   version = "4.11",
   aliases = {"bphysics"},
   description = "Force a block physics update at the target location"
)
public class BlockPhysicsMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   public BlockPhysicsMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(target);
      return SkillResult.SUCCESS;
   }

   public void playEffect(AbstractLocation location) {
      Location l = BukkitAdapter.adapt(location);
      this.getPlugin().getVolatileCodeHandler().getBlockHandler().applyPhysics(l.getBlock());
   }
}
