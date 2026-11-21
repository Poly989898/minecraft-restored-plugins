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
import org.bukkit.entity.Wolf;

@MythicMechanic(
   author = "Ashijin",
   name = "wolfsit",
   aliases = {},
   description = "Sets a wolf's sitting state"
)
public class WolfSitMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "sit",
      aliases = {"state", "value"},
      description = "The sitting state of the wolf",
      defValue = "true"
   )
   protected boolean state;

   public WolfSitMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.state = mlc.getBoolean(new String[]{"sit", "state", "value"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         LivingEntity livingEntity = (LivingEntity)BukkitAdapter.adapt(target);
         if (livingEntity != null && livingEntity instanceof Wolf) {
            ((Wolf)livingEntity).setSitting(this.state);
         }
      }

      return SkillResult.SUCCESS;
   }
}
