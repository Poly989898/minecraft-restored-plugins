package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.concurrent.ThreadLocalRandom;

@MythicCondition(
   author = "Ashijin",
   name = "chance",
   aliases = {},
   version = "5.2",
   description = "Rolls a dice, where 1 = 100%"
)
public class ChanceCondition extends SkillCondition implements ISkillMetaCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "chance",
      aliases = {"c"},
      description = "The chance to be true. 1 = 100%"
   )
   protected PlaceholderDouble chance;

   public ChanceCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.chance = mlc.getPlaceholderDouble(new String[]{"chance", "c"}, "0.5", this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      double chance = this.chance.get(meta);
      return ThreadLocalRandom.current().nextDouble() <= chance;
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      double chance = this.chance.get(meta, target);
      return ThreadLocalRandom.current().nextDouble() <= chance;
   }
}
