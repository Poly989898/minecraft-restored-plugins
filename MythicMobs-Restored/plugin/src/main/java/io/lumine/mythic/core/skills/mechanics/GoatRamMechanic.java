package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Goat;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Phil",
   name = "GoatRam",
   aliases = {},
   description = "Causes the goat to ram the targeted entity"
)
public class GoatRamMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public GoatRamMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!ServerVersion.isPaper()) {
         return SkillResult.INVALID_VERSION;
      } else if (!target.isLiving()) {
         return SkillResult.CONDITION_FAILED;
      } else if (data.getCaster().getEntity().getBukkitEntity() instanceof Goat goat) {
         goat.ram((LivingEntity)target.getBukkitEntity());
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.CONDITION_FAILED;
      }
   }
}
