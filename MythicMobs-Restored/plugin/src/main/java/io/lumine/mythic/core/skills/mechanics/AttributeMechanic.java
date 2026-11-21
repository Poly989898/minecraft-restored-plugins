package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;

@MythicMechanic(
   name = "attribute",
   aliases = {"setAttribute"},
   description = "Sets the mob's attribute"
)
public class AttributeMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   @MythicField(
      name = "attribute",
      aliases = {"attr"},
      defValue = "GENERIC_LUCK",
      description = "The attribute to modify."
   )
   private final String attribute;
   @MythicField(
      name = "amount",
      aliases = {"amt", "a"},
      defValue = "0",
      description = "The amount by which to modify the attribute."
   )
   private final PlaceholderDouble amount;
   @MythicField(
      name = "duration",
      aliases = {"dur"},
      defValue = "0",
      description = "The duration of the attribute modification in ticks."
   )
   private final PlaceholderDuration duration;

   public AttributeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.attribute = mlc.getString(new String[]{"attribute", "attr"}, "GENERIC_LUCK");
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "amt", "a"}, 0.0);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "dur"}, DurationUnit.TICKS, "0");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster().getEntity().getBukkitEntity() instanceof Attributable entity) {
         try {
            Attribute bukkitAttribute = BukkitAttribute.getAttribute(this.attribute);
            if (bukkitAttribute == null) {
               return SkillResult.INVALID_CONFIG;
            } else {
               AttributeInstance attr = entity.getAttribute(bukkitAttribute);
               double original = attr.getBaseValue();
               attr.setBaseValue(this.amount.get(data));
               int duration = this.duration.get(data).getTicks();
               if (duration > 0) {
                  Schedulers.of(data.getCaster().getEntity().getBukkitEntity()).runLater(() -> attr.setBaseValue(original), duration);
               }

               return SkillResult.SUCCESS;
            }
         } catch (Throwable e) {
            e.printStackTrace();
            return SkillResult.INVALID_CONFIG;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Attributable entity) {
         try {
            Attribute bukkitAttribute = Attribute.valueOf(this.attribute);
            double newValue = this.amount.get(data);
            int duration = this.duration.get(data, target).getTicks();
            Entity targetEntity = target.getBukkitEntity();
            Schedulers.runOnRegion(
               targetEntity,
               () -> {
                  AttributeInstance attr = entity.getAttribute(bukkitAttribute);
                  double original = attr.getBaseValue();
                  attr.setBaseValue(newValue);
                  if (duration > 0) {
                     Schedulers.of(data.getCaster().getEntity().getBukkitEntity())
                        .runLater(() -> Schedulers.runOnRegion(targetEntity, () -> attr.setBaseValue(original)), duration);
                  }
               }
            );
            return SkillResult.SUCCESS;
         } catch (Throwable e) {
            e.printStackTrace();
            return SkillResult.INVALID_CONFIG;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
