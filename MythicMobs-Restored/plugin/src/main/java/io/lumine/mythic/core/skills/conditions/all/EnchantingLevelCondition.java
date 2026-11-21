package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "enchantingLevel",
   aliases = {},
   version = "4.8",
   description = "Tests the target's enchanting level"
)
public class EnchantingLevelCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "level",
      aliases = {"l"},
      description = "The level range to match"
   )
   private final RangedInt level;

   public EnchantingLevelCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"level", "l"}, "0", this.conditionVar);
      this.level = new RangedInt(k);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (!e.isPlayer()) {
         return false;
      } else {
         int level = ((Player)e.getBukkitEntity()).getLevel();
         return this.level.equals(level);
      }
   }
}
