package io.lumine.mythic.lib.script.mechanic.offense;

import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.Entity;

@MechanicMetadata
public class AdditiveDamageBuffMechanic extends TargetMechanic {
   private final DoubleFormula amount;
   private final DamageType damageType;

   @Deprecated
   public AdditiveDamageBuffMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("amount");
      this.amount = new DoubleFormula(config.getString("amount"));
      this.damageType = config.contains("damage_type") ? DamageType.valueOf(config.getString("damage_type").toUpperCase()) : null;
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      if (this.damageType == null) {
         meta.getAttackSource().getDamage().additiveModifier(this.amount.evaluate(meta));
      } else {
         meta.getAttackSource().getDamage().additiveModifier(this.amount.evaluate(meta), this.damageType);
      }
   }
}
