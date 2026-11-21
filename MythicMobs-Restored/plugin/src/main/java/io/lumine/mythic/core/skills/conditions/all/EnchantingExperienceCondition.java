package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "enchantingExperience",
   aliases = {"enchantingExp", "enchantExperience", "enchantExp"},
   version = "4.8",
   description = "Tests the target's enchanting experience"
)
public class EnchantingExperienceCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "amount",
      aliases = {"a", "level", "l"},
      description = "The experience range to match"
   )
   private final RangedDouble amount;

   public EnchantingExperienceCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"amount", "a", "level", "l"}, "0", this.conditionVar);
      this.amount = new RangedDouble(k);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (!e.isPlayer()) {
         return false;
      } else {
         float exp = ((Player)e.getBukkitEntity()).getExp();
         return this.amount.equals(exp);
      }
   }
}
