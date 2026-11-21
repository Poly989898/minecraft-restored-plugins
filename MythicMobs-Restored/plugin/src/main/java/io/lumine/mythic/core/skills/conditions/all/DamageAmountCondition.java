package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "DamageAmount",
   aliases = {},
   version = "4.7",
   description = "Checks the damage amount that caused the current skill tree. Only works with onDamaged trigger or aura."
)
public class DamageAmountCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "damagecause",
      aliases = {"amount", "a"},
      description = "The damage cause to match"
   )
   protected PlaceholderString amount;

   public DamageAmountCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = mlc.getPlaceholderString(new String[]{"damageamount", "amount", "a"}, ">0", this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getVariables().has("damage-cause")) {
         double amount = (Double)meta.getVariables().get("damage-amount").get();
         return new RangedDouble(this.amount.get(meta)).equals(amount);
      } else {
         return false;
      }
   }
}
