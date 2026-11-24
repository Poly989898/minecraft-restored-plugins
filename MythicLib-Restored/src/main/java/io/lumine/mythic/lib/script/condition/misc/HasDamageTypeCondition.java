package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;

public class HasDamageTypeCondition extends Condition {
   private final List<DamageType> types = new ArrayList<>();

   public HasDamageTypeCondition(ConfigObject config) {
      super(config);
      config.validateKeys("types");

      for (String str : config.getString("types").split("\\,")) {
         this.types.add(DamageType.valueOf(UtilityMethods.enumName(str)));
      }
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      DamageMetadata damage = meta.getAttackSource().getDamage();

      for (DamageType checked : this.types) {
         if (damage.hasType(checked)) {
            return true;
         }
      }

      return false;
   }
}
