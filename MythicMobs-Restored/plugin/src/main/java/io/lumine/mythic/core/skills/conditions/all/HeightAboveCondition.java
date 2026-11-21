package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "heightAbove",
   aliases = {},
   description = "Checks if the target's Y location is above a value"
)
public class HeightAboveCondition extends SkillCondition implements ISkillMetaLocationComparisonCondition, ILocationCondition {
   @MythicField(
      name = "height",
      aliases = {"h"},
      description = "The height they must be above"
   )
   private final PlaceholderDouble data;

   public HeightAboveCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.data = mlc.getPlaceholderDouble(new String[]{"height", "h"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractLocation l) {
      return l.getY() > this.data.get();
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractLocation l) {
      return l.getY() > this.data.get(PlaceholderContext.builder().meta(meta).build());
   }
}
