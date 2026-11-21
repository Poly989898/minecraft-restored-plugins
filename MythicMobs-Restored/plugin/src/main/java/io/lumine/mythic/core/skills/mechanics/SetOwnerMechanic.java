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
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

@MythicMechanic(
   author = "Ashijin",
   name = "setOwner",
   description = "Sets the caster's owner to the target entity"
)
public class SetOwnerMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public SetOwnerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (data.getCaster() instanceof ActiveMob am) {
         double maxHealth = am.getEntity().getMaxHealth();
         double currentHealth = am.getEntity().getHealth();
         am.setOwnerUUID(target.getUniqueId());
         if (target.isPlayer() && data.getCaster().getEntity().getBukkitEntity() instanceof Tameable tameable) {
            tameable.setOwner((Player)target.getBukkitEntity());
            tameable.setTamed(true);
         }

         am.getEntity().setMaxHealth(maxHealth);
         am.getEntity().setHealth(currentHealth);
         return SkillResult.SUCCESS;
      } else {
         AbstractEntity caster = data.getCaster().getEntity();
         if (caster != null && caster.getBukkitEntity() instanceof Item item) {
            item.setOwner(target.getUniqueId());
         }

         return SkillResult.SUCCESS;
      }
   }
}
