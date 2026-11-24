package io.lumine.mythic.lib.script.mechanic.offense;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

@MechanicMetadata
public class MultiplyDamageMechanic extends TargetMechanic {
   private final DoubleFormula amount;
   private final DamageType damageType;
   private final boolean additive;
   @Nullable
   private final String elementName;

   public MultiplyDamageMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("amount");
      this.amount = new DoubleFormula(config.getString("amount"));
      this.damageType = config.contains("damage_type") ? DamageType.valueOf(config.getString("damage_type").toUpperCase()) : null;
      this.additive = config.getBoolean("additive", false);
      this.elementName = config.contains("element") ? UtilityMethods.enumName(config.getString("element")) : null;
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      Element element = this.elementName == null ? null : MythicLib.plugin.getElements().get(this.elementName);
      if (this.additive) {
         if (element != null) {
            meta.getAttackSource().getDamage().additiveModifier(this.amount.evaluate(meta), element);
         } else if (this.damageType != null) {
            meta.getAttackSource().getDamage().additiveModifier(this.amount.evaluate(meta), this.damageType);
         } else {
            meta.getAttackSource().getDamage().additiveModifier(this.amount.evaluate(meta));
         }
      } else if (element != null) {
         meta.getAttackSource().getDamage().multiplicativeModifier(this.amount.evaluate(meta), element);
      } else if (this.damageType != null) {
         meta.getAttackSource().getDamage().multiplicativeModifier(this.amount.evaluate(meta), this.damageType);
      } else {
         meta.getAttackSource().getDamage().multiplicativeModifier(this.amount.evaluate(meta));
      }
   }
}
