package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Effect;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:ender",
   aliases = {"ender", "e:ender"},
   description = "Creates the ender effect at the target location"
)
public class EnderEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   public EnderEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(target.getLocation());
      return SkillResult.SUCCESS;
   }

   protected void playEffect(AbstractLocation l) {
      BukkitAdapter.adapt(l.getWorld()).playEffect(BukkitAdapter.adapt(l), Effect.ENDER_SIGNAL, 0);
   }
}
