package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.block.Block;
import org.bukkit.entity.Raider;

@MythicMechanic(
   author = "Ashijin",
   name = "setRaiderPatrolBlock",
   aliases = {"setRaiderBlock"},
   version = "5.1",
   description = "Sets the target raider to patrol the given location"
)
public class SetRaiderPatrolBlockMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected SkillTargeter targeter;

   public SetRaiderPatrolBlockMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String target = mlc.getString(new String[]{"location", "l", "block", "b"}, "");
      this.targeter = this.getManager().getTargeter(target);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving() && target.getBukkitEntity() instanceof Raider raider) {
         AbstractLocation block = this.getManager().getLocationTarget(this.targeter, data);
         Block patrolBlock = BukkitAdapter.adapt(block).getBlock();
         Schedulers.runOnRegion(target.getBukkitEntity(), () -> raider.setPatrolTarget(patrolBlock));
      }

      return SkillResult.SUCCESS;
   }
}
