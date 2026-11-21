package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "jaylawl",
   name = "setai",
   aliases = {"ai"},
   description = "Sets whether the mob utilizes AI (ai=true/false)"
)
public class SetAIMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "ai",
      aliases = {"state", "value"},
      description = "Whether AI is enabled for the target mob",
      defValue = "false"
   )
   protected boolean state;

   public SetAIMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.state = mlc.getBoolean(new String[]{"ai", "state", "value"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      LivingEntity livingEntity = (LivingEntity)BukkitAdapter.adapt(target);
      if (livingEntity != null) {
         Schedulers.ensureSync(livingEntity, () -> livingEntity.setAI(this.state));
      }

      return SkillResult.SUCCESS;
   }
}
