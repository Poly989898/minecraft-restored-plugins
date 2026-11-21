package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.heal.HealMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.events.MythicHealMechanicEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.entity.Damageable;

@MythicMechanic(
   author = "Ashijin",
   name = "healpercent",
   aliases = {"percentheal", "hp"},
   description = "Heals the target entity for a percentage of their health"
)
public class HealPercentMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "multiplier",
      aliases = {"m", "a", "amount"},
      description = "The percentage of the target's max health",
      defValue = "0.1"
   )
   protected PlaceholderDouble multiplier;
   @MythicField(
      name = "overHeal",
      aliases = {"oh"},
      description = "Whether or not to apply overhealing as additional max health",
      defValue = "false"
   )
   protected boolean overheal;
   @MythicField(
      name = "maxOverheal",
      aliases = {"maxabsorb", "maxshield", "mo", "ma", "ms"},
      description = "The max amount of additional health",
      defValue = "1"
   )
   protected PlaceholderDouble maxOverheal;
   protected Collection<String> tags = Lists.newArrayList();

   public HealPercentMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.multiplier = mlc.getPlaceholderDouble(new String[]{"multiplier", "m", "a", "amount"}, 0.1);
      this.overheal = mlc.getBoolean(new String[]{"overheal", "oh"}, false);
      this.maxOverheal = mlc.getPlaceholderDouble(new String[]{"maxoverheal", "maxabsorb", "maxshield", "mo", "ma", "ms"}, 0.0);
      String tags = mlc.getString(new String[]{"tags", "tag"}, null);
      if (tags != null) {
         for (String tag : tags.split(",")) {
            this.tags.add(tag.toUpperCase());
         }
      }

      tags = mlc.getString(new String[]{"rawtags", "rtag"}, null);
      if (tags != null) {
         Collections.addAll(this.tags, tags.split(","));
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isDead()) {
         return SkillResult.INVALID_TARGET;
      } else if (target.getBukkitEntity() instanceof Damageable damageable) {
         double value = this.multiplier.get(data, target);
         HealMetadata meta = new HealMetadata(data.getCaster(), data, data.getCasterItem(), target, target.getMaxHealth(), value);
         meta.getTags().addAll(this.tags);
         MythicHealMechanicEvent healEvent = MythicHealMechanicEvent.call(data, meta);
         if (healEvent.isCancelled()) {
            return SkillResult.CONDITION_FAILED;
         } else {
            double health = target.getHealth() + healEvent.getFinalAmount();
            if (health >= target.getMaxHealth()) {
               if (this.overheal) {
                  double max = target.getMaxHealth() * this.maxOverheal.get(data, target);
                  double diff = health - target.getMaxHealth();
                  Schedulers.runOnRegion(target.getBukkitEntity(), () -> {
                     double absorb = damageable.getAbsorptionAmount() + diff;
                     if (absorb < max) {
                        damageable.setAbsorptionAmount(absorb);
                     }
                  });
               }

               target.setHealth(target.getMaxHealth());
            } else {
               if (health < 0.0) {
                  health = 0.0;
               }

               target.setHealth(health);
            }

            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
