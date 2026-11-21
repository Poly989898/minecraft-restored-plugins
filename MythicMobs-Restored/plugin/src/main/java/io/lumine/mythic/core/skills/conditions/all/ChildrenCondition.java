package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "children",
   aliases = {},
   description = "Tests how many children the caster has"
)
public class ChildrenCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of children",
      defValue = "1"
   )
   private final RangedInt amount;

   public ChildrenCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = new RangedInt(mlc.getString(new String[]{"amount", "a"}, "1"));
   }

   @Override
   public boolean check(SkillCaster caster) {
      return this.amount.equals(caster.getChildren().size());
   }
}
