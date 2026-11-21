package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "height",
   aliases = {},
   description = "Checks if the target's Y location is within a range"
)
public class HeightCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "height",
      aliases = {"h"},
      description = "The range to check for"
   )
   private RangedDouble data;

   public HeightCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.data = new RangedDouble(mlc.getString(new String[]{"height", "h"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractLocation l) {
      double height = l.getY();
      return this.data.equals(height);
   }
}
