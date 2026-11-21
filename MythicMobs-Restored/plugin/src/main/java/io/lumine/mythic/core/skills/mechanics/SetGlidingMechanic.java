package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "setGliding",
   description = "Sets the target entity's gliding state"
)
public class SetGlidingMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "gliding",
      aliases = {"g"},
      description = "Whether the target entity should glide",
      defValue = "true"
   )
   protected boolean b;

   public SetGlidingMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.b = mlc.getBoolean(new String[]{"gliding", "g"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         LivingEntity e = (LivingEntity)BukkitAdapter.adapt(target);
         e.setGliding(this.b);
      }

      return SkillResult.SUCCESS;
   }
}
