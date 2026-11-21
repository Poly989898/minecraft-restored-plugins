package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "setleashholder",
   aliases = {"setleasher"},
   description = "Sets an entity to hold the caster's leash"
)
public class SetLeashHolderMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "remove",
      aliases = {"r", "boolean", "bool", "b"},
      description = "Whether to remove the leash holder instead of setting one",
      defValue = "false"
   )
   protected boolean remove;

   public SetLeashHolderMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.remove = mlc.getBoolean(new String[]{"remove", "r", "boolean", "bool", "b"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      LivingEntity caster = (LivingEntity)data.getCaster().getEntity().getBukkitEntity();
      Entity tar = target.getBukkitEntity();
      Runnable applyRunnable = () -> {
         if (this.remove) {
            caster.setLeashHolder(null);
         } else {
            caster.setLeashHolder(tar);
         }
      };
      Schedulers.runOnRegion(caster, applyRunnable);
      return SkillResult.SUCCESS;
   }
}
