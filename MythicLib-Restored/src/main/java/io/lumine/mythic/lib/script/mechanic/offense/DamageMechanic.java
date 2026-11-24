package io.lumine.mythic.lib.script.mechanic.offense;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MechanicMetadata
public class DamageMechanic extends TargetMechanic {
   private final DoubleFormula amount;
   private final boolean knockback;
   private final boolean ignoreImmunity;
   private final DamageType[] types;
   @Nullable
   private final String elementName;

   public DamageMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("amount");
      this.amount = new DoubleFormula(config.getString("amount"));
      this.knockback = config.getBoolean("knockback", true);
      this.ignoreImmunity = config.getBoolean("ignore_immunity", false);
      if (config.contains("damage_type")) {
         String[] split = config.getString("damage_type").split("\\,");
         this.types = new DamageType[split.length];

         for (int i = 0; i < split.length; i++) {
            this.types[i] = DamageType.valueOf(UtilityMethods.enumName(split[i]));
         }
      } else {
         this.types = new DamageType[]{DamageType.MAGIC, DamageType.SKILL};
      }

      this.elementName = config.contains("element") ? UtilityMethods.enumName(config.getString("element")) : null;
   }

   @Override
   public void cast(SkillMetadata meta, @NotNull Entity target) {
      Validate.isTrue(target instanceof LivingEntity, "Cannot damage a non living entity");
      Element element = this.elementName == null ? null : MythicLib.plugin.getElements().get(this.elementName);
      AttackMetadata opt = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target);
      if (opt != null) {
         opt.getDamage().add(this.amount.evaluate(meta), element, this.types);
      } else {
         DamageMetadata damageMetadata = element == null
            ? new DamageMetadata(this.amount.evaluate(meta), this.types)
            : new DamageMetadata(this.amount.evaluate(meta), element, this.types);
         AttackMetadata attackMetadata = new AttackMetadata(damageMetadata, (LivingEntity)target, meta.getCaster());
         MythicLib.plugin.getDamage().registerAttack(attackMetadata, this.knockback, this.ignoreImmunity);
      }
   }
}
