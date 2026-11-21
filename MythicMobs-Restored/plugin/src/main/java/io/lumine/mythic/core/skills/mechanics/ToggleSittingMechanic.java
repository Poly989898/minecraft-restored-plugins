package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sittable;

@MythicMechanic(
   author = "Joshinn",
   name = "togglesitting",
   aliases = {"sit", "petsit", "petsitting"},
   description = "Sets or toggles the sitting state of a sittable entity (wolf, cat, parrot, fox, camel, panda)."
)
public class ToggleSittingMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private final ToggleSittingMechanic.SitMode sitMode;

   public ToggleSittingMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String state = mlc.getString(new String[]{"setSitting", "state"}, "false");
      if ("toggle".equalsIgnoreCase(state)) {
         this.sitMode = ToggleSittingMechanic.SitMode.TOGGLE;
      } else if (Boolean.parseBoolean(state)) {
         this.sitMode = ToggleSittingMechanic.SitMode.TRUE;
      } else {
         this.sitMode = ToggleSittingMechanic.SitMode.FALSE;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isLiving()) {
         return SkillResult.CONDITION_FAILED;
      } else {
         LivingEntity entity = (LivingEntity)BukkitAdapter.adapt(target);
         if (entity instanceof Sittable sittable) {
            boolean next = switch (this.sitMode) {
               case TRUE -> true;
               case FALSE -> false;
               case TOGGLE -> !sittable.isSitting();
            };
            sittable.setSitting(next);
            return SkillResult.SUCCESS;
         } else {
            return SkillResult.CONDITION_FAILED;
         }
      }
   }

   private static enum SitMode {
      TRUE,
      FALSE,
      TOGGLE;
   }
}
