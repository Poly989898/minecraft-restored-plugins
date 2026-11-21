package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Raider;

@MythicMechanic(
   author = "Ashijin",
   name = "setRaiderPatrolLeader",
   aliases = {"setRaiderLeader"},
   version = "5.1",
   description = "Sets the target raider to patrol the given location"
)
public class SetRaiderPatrolLeaderMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "leader",
      aliases = {"b", "bool", "l"},
      description = "Whether the target raider should be patrol leader",
      defValue = "true"
   )
   protected boolean bool;

   public SetRaiderPatrolLeaderMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.bool = mlc.getBoolean(new String[]{"bool", "b", "leader", "l"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving() && target.getBukkitEntity() instanceof Raider raider) {
         Schedulers.runOnRegion(raider, () -> raider.setPatrolLeader(this.bool));
      }

      return SkillResult.SUCCESS;
   }
}
