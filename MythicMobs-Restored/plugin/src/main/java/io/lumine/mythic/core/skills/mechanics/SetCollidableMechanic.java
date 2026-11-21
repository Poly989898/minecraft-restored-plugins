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
   name = "setcollidable",
   description = "Sets whether the mob is collidable"
)
public class SetCollidableMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "collidable",
      aliases = {"c", "state", "value"},
      description = "Whether the target can collide with entities",
      defValue = "false"
   )
   protected boolean state;

   public SetCollidableMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.state = mlc.getBoolean(new String[]{"collidable", "c", "state", "value"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      LivingEntity livingEntity = (LivingEntity)BukkitAdapter.adapt(target);
      if (livingEntity != null) {
         livingEntity.setCollidable(this.state);
      }

      return SkillResult.SUCCESS;
   }
}
