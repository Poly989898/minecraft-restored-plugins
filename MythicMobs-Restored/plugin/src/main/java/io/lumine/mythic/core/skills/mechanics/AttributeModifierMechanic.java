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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;

@MythicMechanic(
   name = "attributeModifier",
   description = "Adds an attribute modifier to the attributable target"
)
public class AttributeModifierMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   @MythicField(
      name = "attribute",
      aliases = {"attr"},
      defValue = "GENERIC_LUCK",
      description = "The attribute to modify."
   )
   private final Attribute attribute;
   @MythicField(
      name = "operation",
      aliases = {"op"},
      defValue = "ADD_NUMBER",
      description = "The operation to perform for the attribute modifier."
   )
   private final Operation operation;
   @MythicField(
      name = "name",
      aliases = {"modifierName"},
      defValue = "<caster.uuid>",
      description = "The name of the attribute modifier."
   )
   private final PlaceholderString name;
   @MythicField(
      name = "amount",
      aliases = {"amt", "a"},
      defValue = "0",
      description = "The amount to modify the attribute."
   )
   private final PlaceholderDouble amount;
   @MythicField(
      name = "duration",
      aliases = {"dur"},
      defValue = "0",
      description = "The duration of the attribute modifier in ticks."
   )
   private final PlaceholderDuration duration;

   public AttributeModifierMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.attribute = BukkitAttribute.getAttribute(mlc.getString(new String[]{"attribute", "attr"}));
      if (this.attribute == null) {
         MythicLogger.errorGenericConfig("Not a valid attribute given");
      }

      this.operation = mlc.getEnum(new String[]{"operation", "op"}, Operation.class, Operation.ADD_NUMBER, "Not a valid operation");
      this.name = mlc.getPlaceholderString(new String[]{"name", "modifierName"}, "<caster.uuid>");
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "amt", "a"}, 0.0);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "dur"}, DurationUnit.TICKS, "0");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.attribute == null) {
         return SkillResult.INVALID_CONFIG;
      } else if (data.getCaster().getEntity().getBukkitEntity() instanceof Attributable entity) {
         AttributeModifier attributeModifier = new AttributeModifier(this.name.get(data), this.amount.get(data), this.operation);
         int duration = this.duration.get(data).getTicks();
         Entity casterEntity = data.getCaster().getEntity().getBukkitEntity();
         Schedulers.runOnRegion(casterEntity, () -> entity.getAttribute(this.attribute).addModifier(attributeModifier));
         if (duration > 0) {
            Schedulers.of(casterEntity)
               .runLater(() -> Schedulers.runOnRegion(casterEntity, () -> entity.getAttribute(this.attribute).removeModifier(attributeModifier)), duration);
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Attributable entity) {
         AttributeModifier attributeModifier = new AttributeModifier(this.name.get(data, target), this.amount.get(data, target), this.operation);
         int duration = this.duration.get(data, target).getTicks();
         Entity targetEntity = target.getBukkitEntity();
         Schedulers.runOnRegion(targetEntity, () -> entity.getAttribute(this.attribute).addModifier(attributeModifier));
         if (duration > 0) {
            Schedulers.of(data.getCaster().getEntity().getBukkitEntity())
               .runLater(() -> Schedulers.runOnRegion(targetEntity, () -> entity.getAttribute(this.attribute).removeModifier(attributeModifier)), duration);
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
