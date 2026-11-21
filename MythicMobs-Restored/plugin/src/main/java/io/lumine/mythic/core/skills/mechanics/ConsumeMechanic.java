package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "consume",
   description = "Deals damage to the target and heals the caster"
)
public class ConsumeMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "damage",
      aliases = {"dmg", "d"},
      description = "The amount of damage to deal to the target",
      defValue = "1"
   )
   protected final PlaceholderFloat damage;
   @MythicField(
      name = "heal",
      aliases = {"h"},
      description = "The amount of health to heal",
      defValue = "1"
   )
   protected final PlaceholderFloat heal;

   public ConsumeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.damage = mlc.getPlaceholderFloat(new String[]{"damage", "dmg", "d"}, 1.0F);
      this.heal = mlc.getPlaceholderFloat(new String[]{"heal", "h"}, 1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && !(target.getHealth() <= 0.0)) {
         LivingEntity consumer = (LivingEntity)data.getCaster().getEntity().getBukkitEntity();
         double damage;
         double healing;
         if (this.powerAffectsDamage) {
            damage = this.damage.get(data, target) * data.getPower();
            healing = this.heal.get(data, target) * data.getPower();
         } else {
            damage = this.damage.get(data, target);
            healing = this.heal.get(data, target);
         }

         this.doDamage(data, target, damage);
         if (consumer.getHealth() + healing >= consumer.getMaxHealth()) {
            consumer.setHealth(consumer.getMaxHealth());
         } else {
            consumer.setHealth(consumer.getHealth() + healing);
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
