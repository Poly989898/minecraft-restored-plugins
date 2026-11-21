package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ash",
   name = "hasEnchantment",
   aliases = {"hasEnchant"},
   description = "Tests if the target entity has an equipped enchantment"
)
public class HasEnchantmentCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "type",
      aliases = {"t", "enchantment", "ench", "e"},
      description = "The enchantment type"
   )
   private final String enchantment;
   @MythicField(
      name = "level",
      aliases = {"lvl", "l"},
      description = "An optional level range to match"
   )
   private RangedDouble level = null;

   public HasEnchantmentCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.enchantment = mlc.getString(new String[]{"type", "enchantment", "ench", "e", "t"}, "ANY").toUpperCase();
      String strLevel = mlc.getString(new String[]{"level", "lvl", "l"}, ">0");
      if (strLevel != null) {
         this.level = new RangedDouble(strLevel);
      }
   }

   @Override
   public boolean check(AbstractEntity entity) {
      int enchantmentLevel = entity.getEnchantmentLevel(this.enchantment);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking if target has Enchantment type = " + this.enchantment + ", level = " + this.level);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning " + enchantmentLevel);
      return this.level.equals(enchantmentLevel);
   }
}
