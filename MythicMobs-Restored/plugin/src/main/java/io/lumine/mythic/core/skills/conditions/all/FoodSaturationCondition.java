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
   author = "Ashijin",
   name = "foodSaturation",
   aliases = {"hungerSaturation"},
   description = "Matches the target's food saturation level"
)
public class FoodSaturationCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "amount",
      aliases = {"a", "food", "f", "saturation", "s"},
      description = "The food range to check for"
   )
   private final RangedDouble amount;

   public FoodSaturationCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = new RangedDouble(mlc.getString(new String[]{"amount", "a", "food", "f", "saturation", "s"}, "0", this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.isPlayer()) {
         double v = entity.asPlayer().getFoodSaturation();
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking FoodSaturation: {0} vs {1}", v, this.amount.toString());
         return this.amount.equals(v);
      } else {
         return false;
      }
   }
}
