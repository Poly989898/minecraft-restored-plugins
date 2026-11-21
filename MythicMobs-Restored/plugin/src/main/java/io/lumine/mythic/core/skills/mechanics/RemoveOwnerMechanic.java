package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Item;
import org.bukkit.entity.Tameable;

@MythicMechanic(
   author = "Ashijin",
   name = "removeowner",
   aliases = {"clearowner"},
   description = ""
)
public class RemoveOwnerMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public RemoveOwnerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (data.getCaster() instanceof ActiveMob am) {
         double maxHealth = am.getEntity().getMaxHealth();
         double currentHealth = am.getEntity().getHealth();
         am.removeOwner();
         if (data.getCaster().getEntity().getBukkitEntity() instanceof Tameable tameable) {
            tameable.setOwner(null);
            tameable.setTamed(false);
         }

         am.getEntity().setMaxHealth(maxHealth);
         am.getEntity().setHealth(currentHealth);
         return SkillResult.SUCCESS;
      } else {
         AbstractEntity caster = data.getCaster().getEntity();
         if (caster != null && caster.getBukkitEntity() instanceof Item item) {
            item.setOwner(null);
         }

         return SkillResult.SUCCESS;
      }
   }
}
